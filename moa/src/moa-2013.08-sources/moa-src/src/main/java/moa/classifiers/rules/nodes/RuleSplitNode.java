<<<<<<< HEAD
/*
 *    RuleSplitNode.java
 *    Copyright (C) 2013 University of Porto, Portugal
 *    @author E. Almeida, A. Carvalho, J. Gama
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *    
 *    
 */
package moa.classifiers.rules.nodes;

import moa.classifiers.core.conditionaltests.InstanceConditionalTest;
import moa.classifiers.rules.core.Predicate;
import moa.classifiers.trees.HoeffdingTree.SplitNode;
import weka.core.Instance;

/**
 * A modified SplitNode method implementing the extra information
 */
public class RuleSplitNode extends SplitNode {

    protected double lastTargetMean;
    protected int operatorObserver;

    private static final long serialVersionUID = 1L;

    public InstanceConditionalTest getSplitTest() {
        return this.splitTest;
    }

    /**
     * Create a new RuleSplitNode
     */
    public RuleSplitNode(InstanceConditionalTest splitTest, double[] classObservations) {
        super(splitTest, classObservations);
    }

    public boolean evaluate(Instance instance) {
        Predicate predicate = (Predicate) this.splitTest;
        return predicate.evaluate(instance);
    }

}
=======
/*
 *    RuleSplitNode.java
 *    Copyright (C) 2013 University of Porto, Portugal
 *    @author E. Almeida, A. Carvalho, J. Gama
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *    
 *    
 */
package moa.classifiers.rules.nodes;

import moa.classifiers.core.conditionaltests.InstanceConditionalTest;
import moa.classifiers.rules.core.Predicate;
import moa.classifiers.trees.HoeffdingTree.SplitNode;
import weka.core.Instance;

/**
 * A modified SplitNode method implementing the extra information
 */
public class RuleSplitNode extends SplitNode {

    protected double lastTargetMean;
    protected int operatorObserver;

    private static final long serialVersionUID = 1L;

    public InstanceConditionalTest getSplitTest() {
        return this.splitTest;
    }

    /**
     * Create a new RuleSplitNode
     */
    public RuleSplitNode(InstanceConditionalTest splitTest, double[] classObservations) {
        super(splitTest, classObservations);
    }

    public boolean evaluate(Instance instance) {
        Predicate predicate = (Predicate) this.splitTest;
        return predicate.evaluate(instance);
    }

}
>>>>>>> 93d8b6469d28597a1fc6ba0371d33fd7d02b1856
