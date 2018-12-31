package trec.experiment;

import clinicaltrial.TrecConfig;
import model.Gene;
import query.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ExperimentsBuilder {
	private Set<Experiment> experiments = new HashSet<>();

	private Experiment buildingExp = null;

	public ExperimentsBuilder() {
	}

	public ExperimentsBuilder newExperiment() {
		validate();
		buildingExp = new Experiment();
		return this;
	}

	public ExperimentsBuilder withName(String name) {
		buildingExp.setExperimentName(name);
		return this;
	}

	public ExperimentsBuilder withTemplate(File template) {
		buildingExp.setDecorator(new ElasticSearchQuery(TrecConfig.ELASTIC_CT_INDEX, new String[] {TrecConfig.ELASTIC_CT_TYPE}));
		Query previousDecorator = buildingExp.getDecorator();
		buildingExp.setDecorator(new TemplateQueryDecorator(template, previousDecorator));
		return this;
	}

	public ExperimentsBuilder withWordRemoval() {
		Query previousDecorator = buildingExp.getDecorator();
		buildingExp.setDecorator(new WordRemovalQueryDecorator(previousDecorator, ExperimentsBuilder.class.getResource("/stopword.txt").getPath()));
		return this;
	}

	public ExperimentsBuilder withDiseaseSynonym() {
		Query previousDecorator = buildingExp.getDecorator();
		buildingExp.setDecorator(new DiseaseSynonymQueryDecorator(previousDecorator));
		return this;
	}

	public ExperimentsBuilder withGeneSynonym() {
		Query previousDecorator = buildingExp.getDecorator();
		buildingExp.setDecorator(new GeneSynonymQueryDecorator(previousDecorator));
		return this;
	}

	public ExperimentsBuilder withDiseaseExpander() {
		Query previousDecorator = buildingExp.getDecorator();
		buildingExp.setDecorator(new DiseaseExpanderQueryDecorator(previousDecorator));
		return this;
	}

	public ExperimentsBuilder withGeneExpansion(Gene.Field[] expandTo) {
		Query previousDecorator = buildingExp.getDecorator();
		buildingExp.setDecorator(new GeneExpanderQueryDecorator(expandTo, previousDecorator));
		return this;
	}

	public ExperimentsBuilder withDiseaseReplacer() {
		Query previousDecorator = buildingExp.getDecorator();
		buildingExp.setDecorator(new DiseaseReplacerQueryDecorator(previousDecorator));
		return this;
	}

	public Set<Experiment> build() {
		validate();
		return experiments;
	}

	private void validate() {
		if (buildingExp != null) {
			this.experiments.add(buildingExp);
			return;
		}

	}
}
