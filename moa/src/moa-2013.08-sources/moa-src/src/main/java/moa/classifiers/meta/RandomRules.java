/*
 *    RandomRules.java
 *    Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 *    @author Albert Bifet (abifet at cs dot waikato dot ac dot nz)
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 *    
 */
package moa.classifiers.meta;

import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.classifiers.Regressor;
import weka.core.Instance;
import moa.core.DoubleVector;
import moa.core.InstancesHeader;
import moa.core.Measurement;
import moa.core.MiscUtils;
import moa.options.ClassOption;
import moa.options.FlagOption;
import moa.options.FloatOption;
import moa.options.IntOption;
import moa.streams.InstanceStream;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;


public class RandomRules extends AbstractClassifier implements Regressor {

    @Override
    public String getPurposeString() {
        return "RandomRules";
    }
        
    private static final long serialVersionUID = 1L;

    public ClassOption baseLearnerOption = new ClassOption("baseLearner", 'l',
            "Classifier to train.", Classifier.class, "rules.AMRules"); 

    public IntOption ensembleSizeOption = new IntOption("ensembleSize", 's',
            "The number of models in the bag.", 10, 1, Integer.MAX_VALUE);

    public FloatOption numAttributesPercentageOption = new FloatOption("numAttributesPercentage", 'n',
            "The number of attributes to use per model.", 63.2, 0, 100); 
    
    public FlagOption useBaggingOption = new FlagOption("useBagging", 'p',
            "Use Bagging.");

    protected Classifier[] ensemble;
    
    protected boolean isRegression;

    @Override
    public void resetLearningImpl() {
        this.ensemble = new Classifier[this.ensembleSizeOption.getValue()];
        Classifier baseLearner = (Classifier) getPreparedClassOption(this.baseLearnerOption);
        baseLearner.resetLearning();
        for (int i = 0; i < this.ensemble.length; i++) {
            this.ensemble[i] = baseLearner.copy();
        }
        this.isRegression = (baseLearner instanceof Regressor);
    }

    @Override
    public void trainOnInstanceImpl(Instance inst) {
        for (int i = 0; i < this.ensemble.length; i++) {
        	int k = 1;
        	if ( this.useBaggingOption.isSet()) {
        		k = MiscUtils.poisson(1.0, this.classifierRandom);
        	} 
        		if (k > 0) {
                Instance weightedInst = transformInstance(inst,i);
                weightedInst.setWeight(inst.weight() * k);
                this.ensemble[i].trainOnInstance(weightedInst);
            }
        }
    }

    @Override
    public double[] getVotesForInstance(Instance inst) {
        DoubleVector combinedVote = new DoubleVector();
        for (int i = 0; i < this.ensemble.length; i++) {
            DoubleVector vote = new DoubleVector(this.ensemble[i].getVotesForInstance(transformInstance(inst,i)));
            if (vote.sumOfValues() != 0.0) {
                if (this.isRegression == false) 
                	vote.normalize();
                combinedVote.addValues(vote);
            }
        }
        if (this.isRegression == true){
        	combinedVote.scaleValues(1.0/this.ensemble.length);
        }
        return combinedVote.getArrayRef();
    }

    @Override
    public boolean isRandomizable() {
        return true;
    }

    @Override
    public void getModelDescription(StringBuilder out, int indent) {
        // TODO Auto-generated method stub
    }

    @Override
    protected Measurement[] getModelMeasurementsImpl() {
        return new Measurement[]{new Measurement("ensemble size",
                    this.ensemble != null ? this.ensemble.length : 0)};
    }

    @Override
    public Classifier[] getSubClassifiers() {
        return this.ensemble; //.clone();
    }

    protected int[][] listAttributes;
    protected int numAttributes;
    protected InstancesHeader[] dataset;
    
    private Instance transformInstance(Instance inst, int classifierIndex) {
    	if (this.listAttributes == null) {
            this.numAttributes = (int) (this.numAttributesPercentageOption.getValue() * inst.numAttributes()/100.0);
            this.listAttributes = new int[this.numAttributes][this.ensemble.length];
            this.dataset = new InstancesHeader[this.ensemble.length];
            for (int ensembleIndex = 0; ensembleIndex < this.ensemble.length; ensembleIndex++) {
               for (int attributeIndex = 0; attributeIndex < this.numAttributes; attributeIndex++) {
                    boolean isUnique = false;
                    while (isUnique == false) {
                        this.listAttributes[attributeIndex][ensembleIndex] = this.classifierRandom.nextInt(inst.numAttributes() - 1);
                        isUnique = true;
                        for (int k = 0; k < attributeIndex; k++) {
                            if (this.listAttributes[attributeIndex][ensembleIndex] == this.listAttributes[k][ensembleIndex]) {
                                isUnique = false;
                                break;
                            }
                        }
                    }
            	   //this.listAttributes[attributeIndex][ensembleIndex] = attributeIndex;
                }
                //Create Header
                FastVector attributes = new FastVector();
                for (int attributeIndex = 0; attributeIndex < this.numAttributes; attributeIndex++) {
                    attributes.addElement(inst.attribute(this.listAttributes[attributeIndex][ensembleIndex]));
                    System.out.print(this.listAttributes[attributeIndex][ensembleIndex]);
                }
                System.out.println("Number of attributes: "+this.numAttributes+ ","+inst.numAttributes());
                attributes.addElement(inst.classAttribute());
                this.dataset[ensembleIndex] =  new InstancesHeader(new Instances(
                getCLICreationString(InstanceStream.class), attributes, 0));
                this.dataset[ensembleIndex].setClassIndex(this.numAttributes);
                this.ensemble[ensembleIndex].setModelContext(this.dataset[ensembleIndex]);
            }
        }
        //Instance instance = new DenseInstance(this.numAttributes+1);
        //instance.setDataset(dataset[classifierIndex]);
        double[] attVals = new double[this.numAttributes + 1];
        for (int attributeIndex = 0; attributeIndex < this.numAttributes; attributeIndex++) {
                //instance.setValue(attributeIndex, inst.value(this.listAttributes[attributeIndex][classifierIndex]));
        		attVals[attributeIndex] = inst.value(this.listAttributes[attributeIndex][classifierIndex]);
        }
        Instance instance = new DenseInstance(1.0, attVals);
        instance.setDataset(dataset[classifierIndex]);
        instance.setClassValue(inst.classValue());
       // System.out.println(inst.toString());
       // System.out.println(instance.toString());
       // System.out.println("============");
        return instance;
    }
}
