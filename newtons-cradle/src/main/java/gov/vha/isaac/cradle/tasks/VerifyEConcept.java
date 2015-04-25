/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.vha.isaac.cradle.tasks;

import gov.vha.isaac.cradle.CradleExtensions;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import gov.vha.isaac.ochre.api.ConceptProxy;
import org.ihtsdo.otf.tcc.dto.TtkConceptChronicle;
import org.ihtsdo.otf.tcc.model.cc.concept.ConceptChronicle;

/**
 *
 * @author kec
 */
public class VerifyEConcept implements Callable<Boolean> {

    private static final AtomicInteger failureCount = new AtomicInteger();

    CradleExtensions termService;
    TtkConceptChronicle eConcept;
    Semaphore permit;

    ConceptProxy newPath = null;
    UUID newPathUuid = null;

    public VerifyEConcept(CradleExtensions termService,
                          TtkConceptChronicle eConcept,
                          Semaphore permit, ConceptProxy newPath) {
        this(termService, eConcept, permit);
        this.newPath = newPath;
        if (this.newPath != null) {
            this.newPathUuid = this.newPath.getUuids()[0];
        }
    }

    public VerifyEConcept(CradleExtensions termService,
            TtkConceptChronicle eConcept,
            Semaphore permit) {
        this.termService = termService;
        this.eConcept = eConcept;
        this.permit = permit;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            if (this.newPath != null) {
                eConcept.processComponentRevisions(r -> r.setPathUuid(newPathUuid));
            }
            int conceptNid = termService.getNidForUuids(eConcept.getPrimordialUuid());

            ConceptChronicle cc = ConceptChronicle.get(conceptNid);
            TtkConceptChronicle remadeEConcept = new TtkConceptChronicle(cc);
            if (!remadeEConcept.equals(eConcept)) {
                StringBuilder builder = new StringBuilder();
                builder.append("\n\nVerify failure: ");
                builder.append(failureCount.incrementAndGet());
                builder.append(" Remade: \n");
                builder.append(remadeEConcept.toString());
                builder.append("\nOriginal: \n");
                builder.append(eConcept);
                builder.append("\n");
                System.err.append(builder.toString());
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        } finally {
            permit.release();
        }
    }
}

