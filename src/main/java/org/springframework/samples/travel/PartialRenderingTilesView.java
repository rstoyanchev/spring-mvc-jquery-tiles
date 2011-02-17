/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.samples.travel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.servlet.context.ServletTilesRequestContextFactory;
import org.apache.tiles.servlet.context.ServletUtil;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.tiles2.TilesView;

/**
 * Detects htmlFormat=nolayout and renders the selected Tiles definition without the surrounding layout.
 */
public class PartialRenderingTilesView extends TilesView {

	private TilesRequestContextFactory tilesRequestContextFactory;

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		tilesRequestContextFactory = new ServletTilesRequestContextFactory();
		tilesRequestContextFactory.init(new HashMap());
	}

	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ServletContext servletContext = getServletContext();
		if ("nolayout".equals(request.getParameter("htmlFormat"))) {

			BasicTilesContainer container = (BasicTilesContainer) ServletUtil.getCurrentContainer(request,
					servletContext);
			if (container == null) {
				throw new ServletException("Tiles container is not initialized. "
						+ "Have you added a TilesConfigurer to your web application context?");
			}

			exposeModelAsRequestAttributes(model, request);
			JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));

			TilesRequestContext tilesRequestContext = tilesRequestContextFactory.createRequestContext(container
					.getApplicationContext(), new Object[] { request, response });
			Definition compositeDefinition = container.getDefinitionsFactory().getDefinition(getUrl(),
					tilesRequestContext);

			Iterator<String> iterator = compositeDefinition.getAttributeNames();
			while (iterator.hasNext()) {
				String attributeName = (String) iterator.next();
				Attribute attribute = compositeDefinition.getAttribute(attributeName);
				if (attribute.getValue() == null || !(attribute.getValue() instanceof String)) {
					continue;
				}
				container.startContext(request, response).inheritCascadedAttributes(compositeDefinition);
				container.render(attribute, request, response);
				container.endContext(request, response);
			}
		} else {
			super.renderMergedOutputModel(model, request, response);
		}
	}

}
