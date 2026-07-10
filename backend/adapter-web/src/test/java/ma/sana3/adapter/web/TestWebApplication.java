package ma.sana3.adapter.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Minimal Spring Boot configuration so @WebMvcTest slices in this module can bootstrap without
 * depending on the real entry point in the bootstrap module.
 */
@SpringBootApplication
class TestWebApplication {}
