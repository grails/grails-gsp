/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.web.servlet.view;

import java.util.Enumeration;
import java.util.Map;
import java.util.Collections;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import grails.core.GrailsApplication;
import grails.core.support.GrailsApplicationAware;
import org.grails.web.sitemesh.FactoryHolder;
import org.grails.web.sitemesh.GroovyPageLayoutFinder;
import org.grails.web.sitemesh.SitemeshLayoutView;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.factory.DefaultFactory;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.compatability.PageParser2ContentProcessor;

public class SitemeshLayoutViewResolver extends GrailsLayoutViewResolver implements GrailsApplicationAware, DisposableBean, Ordered, ApplicationListener<ContextRefreshedEvent>{
    private static final String FACTORY_SERVLET_CONTEXT_ATTRIBUTE = "sitemesh.factory";
    private ContentProcessor contentProcessor;
    protected GrailsApplication grailsApplication;
    private boolean sitemeshConfigLoaded = false;
    private int order = Ordered.LOWEST_PRECEDENCE - 50;
    
    public SitemeshLayoutViewResolver() {
        super();
    }

    public SitemeshLayoutViewResolver(ViewResolver innerViewResolver, GroovyPageLayoutFinder groovyPageLayoutFinder) {
        super(innerViewResolver, groovyPageLayoutFinder);
    }

    @Override
    protected View createLayoutView(View innerView) {
        return new SitemeshLayoutView(groovyPageLayoutFinder, innerView, contentProcessor);
    }
    
    public void init() {
        if(servletContext == null) return;

        Factory sitemeshFactory = (Factory)servletContext.getAttribute(FACTORY_SERVLET_CONTEXT_ATTRIBUTE);
        if(sitemeshFactory==null) {
            sitemeshFactory = loadSitemeshConfig();
        }
        contentProcessor = new PageParser2ContentProcessor(sitemeshFactory);
    }

    protected Factory loadSitemeshConfig() {
        FilterConfig filterConfig=new FilterConfig() {
            private Map<String, String> customConfig =
                    Collections.singletonMap("configFile",
                            "classpath:org/grails/web/sitemesh/sitemesh-default.xml");

            @Override
            public ServletContext getServletContext() {
                return servletContext;
            }
            
            @Override
            public Enumeration<String> getInitParameterNames() {
                return Collections.enumeration(customConfig.keySet());
            }
            
            @Override
            public String getInitParameter(String name) {
                return customConfig.get(name);
            }
            
            @Override
            public String getFilterName() {
                return null;
            }
        };
        Config config = new Config(filterConfig);

        DefaultFactory sitemeshFactory = new DefaultFactory(config);
        if(servletContext != null) {
            servletContext.setAttribute(FACTORY_SERVLET_CONTEXT_ATTRIBUTE, sitemeshFactory);
        }
        sitemeshFactory.refresh();
        FactoryHolder.setFactory(sitemeshFactory);
        sitemeshConfigLoaded = true;
        return sitemeshFactory;
    }

    @Override
    public void setGrailsApplication(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
    }

    @Override
    public void destroy() throws Exception {
        clearSitemeshConfig();
    }

    protected void clearSitemeshConfig() {
        if(servletContext == null) return;
        if(sitemeshConfigLoaded) {
            FactoryHolder.setFactory(null);
            if(servletContext != null) {
                servletContext.removeAttribute(FACTORY_SERVLET_CONTEXT_ATTRIBUTE);
            }
            sitemeshConfigLoaded = false;
        }
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init();
    }
}
