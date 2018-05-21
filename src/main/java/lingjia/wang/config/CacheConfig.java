package lingjia.wang.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableCaching
public class CacheConfig
{
  @Bean(name = "ehCacheManagerFactoryBean")
  public EhCacheManagerFactoryBean ehCacheManagerFactoryBean()
  {
    EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
    ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("config/ehcache.xml"));
    ehCacheManagerFactoryBean.setShared(true);
    return ehCacheManagerFactoryBean;
  }

  @Bean
  public EhCacheCacheManager cacheManager(net.sf.ehcache.CacheManager cacheManager) {

    return new EhCacheCacheManager(cacheManager);
  }

}
