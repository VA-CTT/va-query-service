/*
 * Copyright 2013 International Health Terminology Standards Development Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ihtsdo.otf.query.integration.tests;

import java.io.IOException;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ihtsdo.otf.query.implementation.Clause;
import org.ihtsdo.otf.query.implementation.Query;
import org.ihtsdo.otf.tcc.api.coordinate.StandardViewCoordinates;
import org.ihtsdo.otf.tcc.api.coordinate.Status;
import org.ihtsdo.otf.tcc.api.metadata.binding.Snomed;
import org.ihtsdo.otf.tcc.api.nid.NativeIdSetBI;
import org.ihtsdo.otf.tcc.api.store.Ts;

/**
 * Creates a test for the {@link org.ihtsdo.otf.query.implementation.Xor}
 * clause. Demonstrates the ability to do difference queries.
 *
 * @author dylangrald
 */
public class XorVersionTest extends QueryClauseTest {
    
    SetViewCoordinate setViewCoordinate;
    
    public XorVersionTest() throws IOException {
        this.setViewCoordinate = new SetViewCoordinate(2002, 1, 31, 0, 0);
        this.setViewCoordinate.getViewCoordinate().setAllowedStatus(EnumSet.of(Status.ACTIVE));
        Logger.getLogger(XorVersionTest.class.getName()).log(Level.INFO, "ViewCoordinate in XorVersionTest: {0}", this.setViewCoordinate.getViewCoordinate().toString());
        this.q = new Query(StandardViewCoordinates.getSnomedInferredLatestActiveOnly()) {
            @Override
            protected NativeIdSetBI For() throws IOException {
                return Ts.get().getAllConceptNids();
            }
            
            @Override
            public void Let() throws IOException {
                let("disease", Snomed.DISEASE);
                let("v2", setViewCoordinate.getViewCoordinate());
            }
            
            @Override
            public Clause Where() {
                return Xor(ConceptIsKindOf("disease"), ConceptIsKindOf("disease", "v2"));
            }
        };
        
    }
}
