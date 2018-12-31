package trec.experiment;

import model.Result;
import model.ResultList;
import model.Topic;
import model.TopicSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import query.Query;
import trec.writer.TrecWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Experiment extends Thread {

	private static final Logger LOG = LogManager.getLogger();

	private Query decorator;

	private int year;

	private String experimentName = null;

	@Override
	public void run() {
		final String name = getExperimentId() + " with decorators " + decorator.getName();

		//LOG.info("Running collection " + name + "...");
		System.out.println("程序开始执行...");

		File example = new File(Experiment.class.getResource("/topics/topics" + year + ".xml").getPath());
		TopicSet topicSet = new TopicSet(example);

		File output = new File("results/" + getExperimentId() + ".trec_results");
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
		//LOG.info("Finish...");
		System.out.println("程序执行完毕...");
	}

	public void setDecorator(Query decorator) {
		this.decorator = decorator;
	}

	public String getExperimentId() {
		if (experimentName != null) {
			return experimentName.replace(" ", "_");
		}
		return String.format("%s_%d_%s", "ct", year, decorator.getName().replace(" ", "_"));
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

	public void setYear(int year) {
		this.year = year;
	}

	public Query getDecorator() {
		return decorator;
	}
	
}
