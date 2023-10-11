package com.example.hzv.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AppWorker {

  @JobWorker(type = "notifyForQuarantine")
  public void notifyPersonToQuarantine(final JobClient client, final ActivatedJob job, @Variable String personUuid) {
    log.info("Retrieving contact details for person {} from external database...", personUuid);
    log.info("Sending notification to person {} to quarantine...", personUuid);
  }

  @JobWorker(type = "generateCertificate")
  public void generateCertificateOfRecovery(final JobClient client, final ActivatedJob job, @Variable String personUuid) {
    UUID recoveryCertificateUuid = UUID.randomUUID();
    log.info("Generating certificate of recovery for person {}...", personUuid);
    log.info("Generated certificate ID: {}", recoveryCertificateUuid);
    log.info("Storing Recovery Certificate in external database...");

    client.newCompleteCommand(job.getKey())
        .variables("{\"recoveryCertificateUuid\": \"" + recoveryCertificateUuid + "\"}")
        .send()
        .exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
  }

  @JobWorker(type = "sendCertificate", autoComplete = true)
  public void sendCertificateOfRecovery(final JobClient client, final ActivatedJob job, @Variable String personUuid, @Variable String recoveryCertificateUuid) {
    log.info("Retrieving Recovery Certificate {} from external database...", recoveryCertificateUuid);
    log.info("Retrieving contact details for person {} from external database...", personUuid);
    log.info("Sending Recovery Certificate to person {}. Enjoy that ice-cream!", personUuid);
  }
}
