package org.skytrail.bilcdb.filters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by herndon on 4/14/14.
 */
public class UIFilter implements Filter {

    private final List<Redirect> redirects;

    public UIFilter(List<Redirect> redirects) {
        this.redirects = redirects;
    }

    public UIFilter(Redirect redirect) {
        this(ImmutableList.of(redirect));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) req;
            for (Redirect redirect : redirects) {
                String redirectUrl = redirect.getRedirect(request);
                if (redirectUrl != null) {
                    HttpServletResponse response = (HttpServletResponse) res;

                    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                    response.setHeader(HttpHeaders.LOCATION, redirectUrl);
                    return;
                }
            }
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { /* unused */ }

    public static class Redirect {
        private final Map<String, String> uriMapping;
        private final boolean keepParameters;

        public Redirect(String sourceUri, String targetUri) {
            this(sourceUri, targetUri, true);
        }

        public Redirect(String sourceUri, String targetUri, boolean keepParameters) {
            checkNotNull(sourceUri);
            checkNotNull(targetUri);

            uriMapping = ImmutableMap.of(sourceUri, targetUri);
            this.keepParameters = keepParameters;
        }

        public Redirect(Map<String, String> uriMap) {
            this(uriMap, true);
        }

        public Redirect(Map<String, String> uriMap, boolean keepParameters) {
            checkNotNull(uriMap);

            uriMapping = ImmutableMap.copyOf(uriMap);
            this.keepParameters = keepParameters;
        }

        public String getRedirect(HttpServletRequest request) {
            String uri = uriMapping.get(request.getRequestURI());
            if (uri == null) {
                return null;
            }

            StringBuilder redirect = new StringBuilder(uri);
            if (keepParameters) {
                String query = request.getQueryString();
                if (query != null) {
                    redirect.append('?');
                    redirect.append(query);
                }
            }

            return redirect.toString();
        }
    }
}
