package trec.experiment;

import clinicaltrial.TrecConfig;
import query.ElasticSearchQuery;
import query.Query;
import query.TemplateQueryDecorator;
import query.WordRemovalQueryDecorator;

import java.io.File;

public class ExperimentsBuilder {

	private Experiment buildingExp = null;

	public ExperimentsBuilder() {
	}

	public ExperimentsBuilder newExperiment() {
		buildingExp = new Experiment();
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

	public ExperimentsBuilder withYear(int year) {
		buildingExp.setYear(year);
		return this;
	}

	public Experiment build() {
		return buildingExp;
	}

}
