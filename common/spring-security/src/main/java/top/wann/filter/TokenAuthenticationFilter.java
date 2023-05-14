package top.wann.filter;


import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import top.wann.common.jwt.JwtHelper;
import top.wann.common.result.ResponseUtil;
import top.wann.common.result.Result;
import top.wann.common.result.ResultCodeEnum;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: TokenAuthenticationFilter <br>
 * Package: com.jerry.security.filter <br>
 * Description: 认证解析token过滤器
 *
 * @Author: wann
 * @Create: 2023-03-03 16:01
 * @Version: 1.0
 */

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;

    public TokenAuthenticationFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        logger.info("uri:" + request.getRequestURI());
        //如果是登录接口，直接放行
        if ("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // token置于header里
        String token = request.getHeader("token");
        logger.info("token:" + token);
        if (!StringUtils.isEmpty(token)) {
            String username = JwtHelper.getUsername(token);
            logger.info("username:" + username);
            if (!StringUtils.isEmpty(username)) {
                String authoritys =
                        redisTemplate.opsForValue().get(TokenLoginFilter.AUTHORITY_KEY + username);
                if (!StringUtils.isEmpty(authoritys)) {
                    List<Map> maps = JSON.parseArray(authoritys, Map.class);
                    List<SimpleGrantedAuthority> authorityList = maps
                            .stream()
                            .map((map -> new SimpleGrantedAuthority((String) map.get("authority"))))
                            .collect(Collectors.toList());
                    return new UsernamePasswordAuthenticationToken(username, null, authorityList);
                } else {
                    return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                }


            }

        }
        return null;
    }
}
