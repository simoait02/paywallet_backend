package com.paylogic.paywalletlite;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.paylogic.paywalletlite.config.root.RootConfig;

public class PayWalletLiteApplication {

    public static void main(String[] args) {
        // Pour tests unitaires / intégration
        // En production : démarrage via Tomcat + WebAppInitializer

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(RootConfig.class);

        System.out.println("PayWallet Lite Backend Started");
        System.out.println("Beans loaded: " + context.getBeanDefinitionCount());
    }
}