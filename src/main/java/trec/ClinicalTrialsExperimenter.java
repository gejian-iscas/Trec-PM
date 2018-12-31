package trec;


import clinicaltrial.ClinicalTrial;
import clinicaltrial.TrecConfig;
import model.Gene;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import search.ElasticClientFactory;
import trec.experiment.Experiment;
import trec.experiment.ExperimentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJson;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ClinicalTrialsExperimenter {
	public static void main(String[] args) {
//		try {
//			indexClinicalTrials("/Users/jian-0526/Desktop/TREC/clinicaltrials_xml");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		final File cancerSynonymsTemplate = new File(
				ClinicalTrialsExperimenter.class.getResource("/clinical_trials/template.json").getFile());
		final Gene.Field[] expandTo = { Gene.Field.SYMBOL, Gene.Field.SYNONYMS };

		ExperimentsBuilder builder = new ExperimentsBuilder();

		builder.newExperiment().withName("topics2017-1").withTemplate(cancerSynonymsTemplate).withWordRemoval().withGeneExpansion(expandTo);
		builder.newExperiment().withName("topics2017-2").withTemplate(cancerSynonymsTemplate).withWordRemoval().withGeneExpansion(expandTo);
		builder.newExperiment().withName("topics2017-3").withTemplate(cancerSynonymsTemplate).withWordRemoval().withGeneExpansion(expandTo);
		builder.newExperiment().withName("topics2017-4").withTemplate(cancerSynonymsTemplate).withWordRemoval().withGeneExpansion(expandTo);
		builder.newExperiment().withName("topics2017-5").withTemplate(cancerSynonymsTemplate).withWordRemoval().withGeneExpansion(expandTo);

		Set<Experiment> experiments = builder.build();

		for (Experiment exp : experiments) {
			exp.start();
			try {
				exp.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static long indexClinicalTrials(String dataFolderWithFiles) throws Exception {
		System.out.println("开始建立索引...");

		long startTime = System.currentTimeMillis();

		BulkProcessor bulkProcessor = buildBuildProcessor();
		Files.walk(Paths.get(dataFolderWithFiles))
				.filter(Files::isRegularFile)
				.forEach(file -> {
					String fileName = file.toString();
					if (fileName.endsWith(".xml")) {
						ClinicalTrial trial = getClinicalTrialFromFile(file.toString());
						System.out.println("添加文件: " + trial.id);

						try {
							bulkProcessor.add(new IndexRequest(TrecConfig.ELASTIC_CT_INDEX, TrecConfig.ELASTIC_CT_TYPE, trial.id)
									.source(buildJson(trial)));
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				});

		bulkProcessor.awaitClose(10, TimeUnit.MINUTES);

		long indexingDuration = (System.currentTimeMillis() - startTime);

		System.out.println("索引耗时: " + indexingDuration/1000 + " 秒");
		System.out.println("索引建立完毕...");
		return indexingDuration;
	}

	private static BulkProcessor buildBuildProcessor() {
		Client client = ElasticClientFactory.getClient();

		return BulkProcessor.builder(
				client,
				new BulkProcessor.Listener() {
					@Override
					public void beforeBulk(long executionId,
										   BulkRequest request) {

					}

					@Override
					public void afterBulk(long executionId,
										  BulkRequest request,
										  BulkResponse response) {
						if (response.hasFailures()) {
							throw new RuntimeException(response.buildFailureMessage());
						}
					}

					@Override
					public void afterBulk(long executionId,
										  BulkRequest request,
										  Throwable failure) {
						throw new RuntimeException(failure);
					}
				}).build();
	}

	private static XContentBuilder buildJson(ClinicalTrial trial) throws IOException {
		return jsonBuilder()
				.startObject()
				.field("id", trial.id)
				.field("brief_title", escapeJson(trial.brief_title))
				.field("official_title", escapeJson(trial.official_title))
				.field("summary", escapeJson(trial.summary))
				.field("description", escapeJson(trial.description))
				.field("studyType", trial.studyType)
				.field("interventionModel", trial.interventionModel)
				.field("primary_purpose", trial.primaryPurpose)
				.field("outcomeMeasures", trial.outcomeMeasures)
				.field("outcomeDescriptions", trial.outcomeDescriptions)
				.field("conditions", trial.conditions)
				.field("interventionTypes", trial.interventionTypes)
				.field("interventionNames", trial.interventionNames)
				.field("armGroupDescriptions", trial.armGroupDescriptions)
				.field("sex", trial.sex)
				.field("minimum_age", trial.minAge)
				.field("maximum_age", trial.maxAge)
				.field("inclusion", escapeJson(trial.inclusion))
				.field("exclusion", escapeJson(trial.exclusion))
				.field("keywords", trial.keywords)
				.field("meshTags", trial.meshTags)
				.endObject();
	}

	public static ClinicalTrial getClinicalTrialFromFile(String xmlTrialFileName) {

		return(ClinicalTrial.fromXml(xmlTrialFileName));
	}

}
