package myspring.springframework.context.annotation;

import cn.hutool.core.util.StrUtil;
import myspring.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import myspring.springframework.beans.factory.config.BeanDefinition;
import myspring.springframework.beans.factory.support.BeanDefinitionRegistry;
import myspring.springframework.stereotype.Component;
import java.util.Set;

/**
 * @author Ryan
 */
public class ClassPathBeanDefinitionScanner extends ClasspathScanningCandidateComponentProvider{

    private BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry){
        this.registry = registry;
    }

    public void doScan(String... basePackages){
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                // 解析 Bean 的作用域 singleton、prototype
                String s = resolveBeanScope(candidateComponent);
                if (StrUtil.isNotEmpty(s)){
                    candidateComponent.setScope(s);
                }
                registry.registerBeanDefinition(determineBeanName(candidateComponent), candidateComponent);
            }
        }
        // 注册处理注解的 BeanPostProcessor（@Autowired、@Value）
        registry.registerBeanDefinition("myspring.springframework.context.annotation.internalAutowiredAnnotationProcessor", new BeanDefinition(AutowiredAnnotationBeanPostProcessor.class));
    }

    private String resolveBeanScope(BeanDefinition beanDefinition){
        Class beanClass = beanDefinition.getBeanClass();
        Scope annotation = (Scope) beanClass.getAnnotation(Scope.class);
        if ( null != annotation){
            return annotation.value();
        }
        return StrUtil.EMPTY;
    }

    private String determineBeanName(BeanDefinition beanDefinition){
        Class beanClass = beanDefinition.getBeanClass();
        Component annotation = (Component) beanClass.getAnnotation(Component.class);
        String value = annotation.value();
        if (StrUtil.isEmpty(value)){
            value = StrUtil.lowerFirst(beanClass.getSimpleName());
        }
        return value;
    }
}
