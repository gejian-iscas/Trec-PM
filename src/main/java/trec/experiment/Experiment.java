package trec.experiment;

import model.Result;
import model.ResultList;
import model.Topic;
import model.TopicSet;
import query.Query;
import trec.writer.TrecWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Experiment extends Thread {

	private Query decorator;

	private String experimentName = null;

	@Override
	public void run() {
		System.out.println(experimentName + "程序开始执行...");

		File example = new File(Experiment.class.getResource("/topics/" + experimentName + ".xml").getPath());
		TopicSet topicSet = new TopicSet(example);

		File output = new File("results/output-" + experimentName + ".txt");
		final String runName = getExperimentName();
		TrecWriter tw = new TrecWriter(output, runName);

		List<ResultList> resultListSet = new ArrayList<>();
		for (Topic topic : topicSet.getTopics()) {
			List<Result> results = decorator.query(topic);

			ResultList resultList = new ResultList(topic);
			for (Result result : results) {
				resultList.add(result);
			}
			resultListSet.add(resultList);
		}

		tw.write(resultListSet);
		tw.close();
		System.out.println(experimentName + "程序执行完毕...");
	}

	public void setDecorator(Query decorator) {
		this.decorator = decorator;
	}

	public void setExperimentName(String name) {
		this.experimentName = name;
	}

	public String getExperimentName() {
		if (experimentName == null) {
			return "experiment";
		}

		return experimentName;
	}

	public Query getDecorator() {
		return decorator;
	}
	
}
