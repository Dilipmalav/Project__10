package com.rays.common;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.rays.config.JWTUtil;
import com.rays.service.JWTUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * Front controller verifies if user id logged in
 * 
 * Dilip Malav 
 * 
 */
@Component
public class FrontCtl extends HandlerInterceptorAdapter {
	@Autowired
	private JWTUserDetailsService jwtUserDetailsService;

	@Autowired
	private JWTUtil jwtUtil;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		
		
		 HttpSession session = request.getSession(); 
		String path = request.getServletPath();
		
		/*
		 * System.out.println(" Front Ctl Called " + path); if
		 * (!path.startsWith("/Auth/")) {
		 * 
		 * System.out.println("2222");
		 * 
		 * if (session.getAttribute("user") == null) { System.out.println("3333");
		 * response.setContentType("application/json");
		 * response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		 * response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
		 * response.setHeader("Access-Control-Allow-Credentials", "true");
		 * response.setHeader("Access-Control-Allow-Methods",
		 * "GET,DELETE,OPTIONS,POST,PUT");
		 * response.setHeader("Access-Control-Allow-Headers","*");
		 * 
		 * PrintWriter out = response.getWriter();
		 *  out. print("{\"success\":\"false\",\"error\":\"OOPS! Your session has been expired\"}"
		 * ); out.close(); System.out.println("going to return false "); return false; }
		 * System.out.println("4444"); return true; } System.out.println("5555"); return
		 * false;
		 */
		
		
		
		boolean pass= false;
		if (!path.startsWith("/Auth/")) {
		//	System.out.println("inside if condition");
			
		
		System.out.println("JWTRequestFilter run success");
		final String requestTokenHeader = request.getHeader("Authorization");
		System.out.println(requestTokenHeader+"]]]]]]]]]]---------------");
		String username = null;
		String jwtToken = null;
		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			System.out.println("Inside token != null");
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtUtil.extractUsername(jwtToken);
				System.out.println(username+" user-------------");
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			}
		} else {
			System.out.println("JWT Token does not begin with Bearer String");
			
		}

		//Once we get the token validate it.
		if (username != null ) {
			System.out.println("inside user != null");
			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			// if token is valid configure Spring Security to manually set authentication
			if (jwtUtil.validateToken(jwtToken)) {

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// After setting the Authentication in the context, we specify
				// that the current user is authenticated. So it passes the Spring Security Configurations successfully.
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
			pass = true;
		}
		}
		return pass;
	}
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("inside post handler");
		response.setHeader("Access-Control-Allow-Origin", "");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
	}
}

