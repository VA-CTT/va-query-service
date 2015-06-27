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

import gov.vha.isaac.metadata.coordinates.ViewCoordinates;
import java.io.IOException;
import org.ihtsdo.otf.query.implementation.Clause;
import org.ihtsdo.otf.query.implementation.ComponentCollectionTypes;
import org.ihtsdo.otf.query.implementation.ForSetSpecification;
import org.ihtsdo.otf.query.implementation.Query;

/**
 *
 * @author dylangrald
 */
public class DescriptionActiveLuceneMatchTest extends QueryClauseTest {

    public DescriptionActiveLuceneMatchTest() throws IOException {
        this.q = new Query(ViewCoordinates.getDevelopmentInferredLatestActiveOnly()) {
            @Override
            protected ForSetSpecification ForSetSpecification() {
                return new ForSetSpecification(ComponentCollectionTypes.ALL_CONCEPTS);
            }

            @Override
            public void Let() {
                let("baranys", "barany's");
            }

            @Override
            public Clause Where() {
                return ConceptForComponent(DescriptionActiveLuceneMatch("baranys"));
            }
        };
    }
}
