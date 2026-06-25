package org.racetobid.racetobid.interceptor;

import org.racetobid.racetobid.service.impl.JwtTokenService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenService jwtTokenService;

    // Kullanıcı detaylarını yüklemek için UserDetailsService'e ihtiyacın olabilir
    // Ancak sadece token geçerliliği yetiyorsa gerek yok.

    public JwtHandshakeInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            URI uri = request.getURI();
            String query = uri.getQuery(); // "token=xyz..." kısmını alır
            String token = getTokenFromQuery(query);

            if (token != null && !token.isEmpty()) {
                String email = jwtTokenService.extractEmail(token);

                // Burada istersen veritabanından kullanıcıyı da kontrol edebilirsin
                // Şimdilik sadece token süresi ve imza kontrolü yapıyoruz:
                if (email != null && !jwtTokenService.isTokenExpired(token)) {

                    // WebSocket Session boyunca bu kullanıcıyı tanımak için attribute ekliyoruz
                    attributes.put("username", email);

                    return true; // Bağlantıya izin ver
                }
            }

            // Hata durumunda log basabilirsin
            System.out.println("WebSocket bağlantısı reddedildi: Geçersiz Token");
            return false;

        } catch (Exception e) {
            System.out.println("WebSocket handshake hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Genelde boş bırakılır, bağlantı sonrası loglama için kullanılabilir.
    }

    // Query string'den token ayıklama yardımcısı
    private String getTokenFromQuery(String query) {
        if (query != null && query.contains("token=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    return param.substring(6); // "token=" uzunluğu 6
                }
            }
        }
        return null;
    }
}
