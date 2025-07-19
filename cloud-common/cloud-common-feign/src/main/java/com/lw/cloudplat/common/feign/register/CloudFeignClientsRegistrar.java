package com.lw.cloudplat.common.feign.register;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.cloud.openfeign.FeignClientSpecification;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Feign客户端注册
 * 扫描 META-INF/spring/org.springframework.cloud.openfeign.FeignClient.imports 文件中的配置
 * 处理参考逻辑：FeignClientsRegistrar
 * @author lw
 * @create 2025-07-19-14:24
 */
@Slf4j
public class CloudFeignClientsRegistrar implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware, EnvironmentAware {

    private final static String BASE_URL = "http://127.0.0.1:${server.port}${server.servlet.context-path}";

    @Getter
    private ClassLoader beanClassLoader;
    @Getter
    private Environment environment;

    /**
     * 注册自定义bean
     * @param importingClassMetadata annotation metadata of the importing class
     * @param registry current bean definition registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerFeignClients(registry);
    }

    /**
     * 注册Feign客户端到Spring容器
     * @param registry
     */
    private void registerFeignClients(BeanDefinitionRegistry registry) {
        List<String> feignClients = new ArrayList<>();
        ImportCandidates.load(FeignClient.class, this.beanClassLoader).forEach(feignClients::add);

        if (feignClients.isEmpty()) {
            return;
        }

        for (String feignClientClassName : feignClients) {
            try {
                Class<?> clazz = beanClassLoader.loadClass(feignClientClassName);
                AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(clazz, FeignClient.class);
                if (Objects.isNull(attributes)) {
                    continue;
                }

                Boolean isMicro = environment.getProperty("spring.cloud.nacos.discovery.enabled", Boolean.class, true);
                // 如果已经注册就不需要重新注册
                if (registry.containsBeanDefinition(feignClientClassName) && isMicro) {
                    continue;
                }

                // 注册FeignClient
                registerClientConfiguration(registry, getClientName(attributes), feignClientClassName,
                        attributes.get("configuration"));

                //注册BeanDefinition
                registerFeignClientBeanDefinition(feignClientClassName, attributes, registry);

            } catch (ClassNotFoundException e) {
                log.error("{} 类加载失败: {}", feignClientClassName, e.getMessage(), e);
            }
        }
    }

    private void registerFeignClientBeanDefinition(String className, Map<String, Object> attributes,
                                                   BeanDefinitionRegistry registry) {
        validate(attributes);
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(FeignClientFactoryBean.class);
        definition.addPropertyValue("url", getUrl(attributes));
        definition.addPropertyValue("path", getPath(attributes));
        String name = getName(attributes);
        definition.addPropertyValue("name", name);

        // 兼容最新版本的 spring-cloud-openfeign，尚未发布
        StringBuilder aliasBuilder = new StringBuilder(18);
        if (attributes.containsKey("contextId")) {
            String contextId = getContextId(attributes);
            aliasBuilder.append(contextId);
            definition.addPropertyValue("contextId", contextId);
        }
        else {
            aliasBuilder.append(name);
        }

        definition.addPropertyValue("type", className);
        definition.addPropertyValue("dismiss404",
                Boolean.parseBoolean(String.valueOf(attributes.get("dismiss404"))));
        Object fallbackFactory = attributes.get("fallbackFactory");
        if (fallbackFactory != null) {
            definition.addPropertyValue("fallbackFactory", fallbackFactory instanceof Class ? fallbackFactory
                    : ClassUtils.resolveClassName(fallbackFactory.toString(), null));
        }
        definition.addPropertyValue("fallbackFactory", attributes.get("fallbackFactory"));
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();

        // alias
        String alias = aliasBuilder.append("FeignClient").toString();

        // has a default, won't be null
        boolean primary = (Boolean) attributes.get("primary");

        beanDefinition.setPrimary(primary);

        String qualifier = getQualifier(attributes);
        if (StringUtils.hasText(qualifier)) {
            alias = qualifier;
        }

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[] { alias });
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private void validate(Map<String, Object> attributes) {
        AnnotationAttributes annotation = AnnotationAttributes.fromMap(attributes);
        validateFallback(annotation.getClass("fallback"));
        validateFallbackFactory(annotation.getClass("fallbackFactory"));
    }

    static void validateFallback(final Class clazz) {
        Assert.isTrue(!clazz.isInterface(), "Fallback class must implement the interface annotated by @FeignClient");
    }

    static void validateFallbackFactory(final Class clazz) {
        Assert.isTrue(!clazz.isInterface(), "Fallback factory must produce instances "
                + "of fallback classes that implement the interface annotated by @FeignClient");
    }

    private String getContextId(Map<String, Object> attributes) {
        String contextId = (String) attributes.get("contextId");
        if (!StringUtils.hasText(contextId)) {
            return getName(attributes);
        }

        contextId = resolve(contextId);
        return getName(contextId);
    }

    @Nullable
    private String getQualifier(@Nullable Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String qualifier = (String) client.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return qualifier;
        }
        return null;
    }

    private String getPath(Map<String, Object> attributes) {
        String path = resolve((String) attributes.get("path"));
        return getPath(path);
    }

    private String getPath(String path) {
        if (StringUtils.hasText(path)) {
            path = path.trim();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
        }
        return path;
    }

    private String getUrl(Map<String, Object> attributes) {

        // 如果是单体项目自动注入 & url 为空
        Boolean isMicro = environment.getProperty("spring.cloud.nacos.discovery.enabled", Boolean.class, true);

        if (isMicro) {
            return null;
        }

        Object objUrl = attributes.get("url");

        String url = "";
        if (StringUtils.hasText(objUrl.toString())) {
            url = resolve(objUrl.toString());
        }
        else {
            url = resolve(BASE_URL);
        }

        return getUrl(url);
    }

    private String getUrl(String url) {
        if (StringUtils.hasText(url) && !(url.startsWith("#{") && url.contains("}"))) {
            if (!url.contains("://")) {
                url = "http://" + url;
            }
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            try {
                new URI(url);
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException(url + " is malformed", e);
            }
        }
        return url;
    }

    private String getName(Map<String, Object> attributes) {
        String name = (String) attributes.get("serviceId");
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("name");
        }
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("value");
        }
        name = resolve(name);
        return getName(name);
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    private String getName(String name) {
        if (!StringUtils.hasText(name)) {
            return "";
        }

        String host = null;
        try {
            String url;
            if (!name.startsWith("http://") && !name.startsWith("https://")) {
                url = "http://" + name;
            }
            else {
                url = name;
            }
            host = new URI(url).getHost();

        }
        catch (URISyntaxException ignored) {
        }
        Assert.state(host != null, "Service id not legal hostname (" + name + ")");
        return name;
    }

    /**
     * 获取bean name，依次从contextId、value、name、serviceId字段中获取
     * @param client
     * @return
     */
    private String getClientName(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String value = (String) client.get("contextId");
        if (!StringUtils.hasText(value)) {
            value = (String) client.get("value");
        }
        if (!StringUtils.hasText(value)) {
            value = (String) client.get("name");
        }
        if (!StringUtils.hasText(value)) {
            value = (String) client.get("serviceId");
        }
        if (StringUtils.hasText(value)) {
            return value;
        }

        throw new IllegalStateException(
                "Either 'name' or 'value' must be provided in @" + FeignClient.class.getSimpleName());
    }

    /**
     * 注册客户端配置到BeanDefinitionRegistry
     * @param registry
     * @param name
     * @param className
     * @param configuration
     */
    private void registerClientConfiguration(BeanDefinitionRegistry registry, Object name, Object className,
                                             Object configuration) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FeignClientSpecification.class);
        builder.addConstructorArgValue(name);
        builder.addConstructorArgValue(className);
        builder.addConstructorArgValue(configuration);
        registry.registerBeanDefinition(name + "." + FeignClientSpecification.class.getSimpleName(),
                builder.getBeanDefinition());
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


}
