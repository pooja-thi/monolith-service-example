package com.holobuilder.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Properties specific to Pooja Thittamaranahalli Backendtasks.
 *
 * Properties are configured in the `application.yml` file.
 * See [tech.jhipster.config.JHipsterProperties] for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
class ApplicationProperties
