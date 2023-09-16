package br.com.challenge.picpay.services;

import br.com.challenge.picpay.domain.user.User;
import br.com.challenge.picpay.dtos.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
    //private static final String NOTIFICATION_URL = "http://o4d9z.mocklab.io/notify";
    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(User user, String message) throws Exception {
        String email = user.getEmail();
        NotificationDTO notificationRequest = new NotificationDTO(email, message);

        /*HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<NotificationDTO> requestEntity = new HttpEntity<>(notificationRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                NOTIFICATION_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Serviço de notificação está fora do ar");
        }*/
    }
}
