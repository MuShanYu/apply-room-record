package top.mushanyu.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;
import top.mushanyu.web.config.converter.Jackson2HttpMessageConverter;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(1, new Jackson2HttpMessageConverter());
    }

//    @Override
//    public void configurePathMatch(PathMatchConfigurer configurer) {
//        UrlPathHelper urlPathHelper = new UrlPathHelper();
//        urlPathHelper.setAlwaysUseFullPath(true);
//        configurer.setUrlPathHelper(urlPathHelper);
//        configurer.addPathPrefix("arr-web", t -> true);
//    }
}
