package br.com.ponto.config;

import br.com.ponto.service.AutenticacaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AutenticacaoInterceptor implements HandlerInterceptor {

    public static final String USUARIO_LOGADO = "usuarioLogado";

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoInterceptor(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Faça login para continuar");
        }
        String token = header.substring(7).trim();
        request.setAttribute(USUARIO_LOGADO, autenticacaoService.buscarPorToken(token));
        return true;
    }
}
