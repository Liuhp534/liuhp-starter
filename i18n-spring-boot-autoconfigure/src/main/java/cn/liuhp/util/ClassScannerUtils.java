package cn.liuhp.util;

import cn.liuhp.core.EnumI18nInterface;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/*
 *  获取指定目录下的包的工具
 * */
public class ClassScannerUtils implements ResourceLoaderAware {

    //保存过滤规则要排除的注解
    private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();
    private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

    public static Set<Class> scan(String[] basePackages,
                                  Class<? extends Annotation>... annotations) {
        ClassScannerUtils cs = new ClassScannerUtils();

        if (null != annotations) {
            for (Class anno : annotations) {
                cs.addIncludeFilter(new AnnotationTypeFilter(anno));
            }
        }

        Set<Class> classes = new HashSet<Class>();
        for (String s : basePackages)
            classes.addAll(cs.doScan(s));
        return classes;
    }

    public static Set<Class> scan(String basePackages, Class<? extends Annotation>... annotations) {
        return ClassScannerUtils.scan(StringUtils.tokenizeToStringArray(basePackages, ",; \t\n"), annotations);
    }

    public final ResourceLoader getResourceLoader() {
        return this.resourcePatternResolver;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils
                .getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(
                resourceLoader);
    }

    public void addIncludeFilter(TypeFilter includeFilter) {
        this.includeFilters.add(includeFilter);
    }

    public void addExcludeFilter(TypeFilter excludeFilter) {
        this.excludeFilters.add(0, excludeFilter);
    }

    public void resetFilters(boolean useDefaultFilters) {
        this.includeFilters.clear();
        this.excludeFilters.clear();
    }

    public Set<Class> doScan(String basePackage) {
        Set<Class> classes = new HashSet<Class>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils
                    .convertClassNameToResourcePath(SystemPropertyUtils
                            .resolvePlaceholders(basePackage))
                    + "/**/*.class";
            Resource[] resources = this.resourcePatternResolver
                    .getResources(packageSearchPath);

            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
                if (resource.isReadable()) {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    if ((includeFilters.size() == 0 && excludeFilters.size() == 0)
                            || matches(metadataReader)) {
                        try {
                            classes.add(Class.forName(metadataReader
                                    .getClassMetadata().getClassName()));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException(
                    "I/O failure during classpath scanning", ex);
        }
        return classes;
    }

    protected boolean matches(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : this.includeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return true;
            }
        }
        return false;
    }

    public static List<EnumI18nInterface> analysisPackage(String packageStr) throws Exception {
        List<EnumI18nInterface> allEnums = new ArrayList<>(256);
        Set<Class> sets = ClassScannerUtils.scan(packageStr);
        try {
            for (Class clazz : sets) {
                if (isEnumInterface(clazz)) {
                    EnumI18nInterface[] temps = (EnumI18nInterface[]) clazz.getEnumConstants();
                    allEnums.addAll(Arrays.asList(temps));
                }
            }
        } catch (Exception e) {
            throw new Exception("analysis package exception", e);
        }
        return allEnums;
    }

    private static boolean isEnumInterface(Class clazz) {
        if (!clazz.isEnum()) {
            return Boolean.FALSE;
        }
        Class[] clazzInterfaces = clazz.getInterfaces();
        for (Class c : clazzInterfaces) {
            if (c.getName().equalsIgnoreCase(EnumI18nInterface.class.getName())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public static void main(String[] args) {
        Set<Class> sets = ClassScannerUtils.scan("cn.liuhp.**", null);

        for (Class c : sets) {
            System.out.println(c.getName());
        }
    }
}
