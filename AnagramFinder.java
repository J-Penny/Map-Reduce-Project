import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.util.Hashtable;
import java.util.Arrays;

public class AnagramFinder {
	private static String not_letters = "[^a-zA-Z\u00C0-\u017F]";
  private static String not_word_chars = "(?<= )'|[^a-zA-Z\u00C0-\u017F '-]";
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(WordCount.class);
		job.setMapperClass(AMapper.class);
		job.setReducerClass(AReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(String[].class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		System.out.println(Arrays.toString(get_words("This is a sentence listen, take a break............... from the chicken in the don't e-mail. Change the course--taken_list?")));
	}

	public static class AMapper
		extends Mapper<Object, Text, Text, Text>{

		private Text formated_word = new Text();

		public void map(Object key, Text value, Context context)
		 throws IOException, InterruptedException {
			Text[] words = get_words(value.toString());
			for(Text word : words) {
				formated_word.set(format_word(word));
				context.write(formated_word, word);
			}
		}
	}

	public static class AReducer
		extends Reducer<Text, Text[], Text, Text[]> {

		public void reduce(Text key, Iterable<String> values, Context context)
		 throws IOException, InterruptedException {
			String[] anagrams = {};
			for (String val : values) {
				if(inArray(val, anagrams)) continue;

				anagrams = push(anagrams, val);
			}

			if(anagrams.length > 1) {
				context.write(key, anagrams);
			}
		}
	}

  private static String[] get_words(String text) {
    text = text.toLowerCase();
    text = text.replaceAll(not_word_chars,"");
    String[] words = text.split(" +|--");
    return words;
  }

  private static String format_word(String word) { 
    word = word.replaceAll(not_letters, "");
    return alphabetise(word);
  }

  private static String alphabetise(String input) {
    char[] word = input.toLowerCase().toCharArray();
    Arrays.sort(word);
    return new String(word);
  }

  private static String[] push(String[] array, String string_to_push){
    final int n = array.length;
    array = Arrays.copyOf(array, n + 1);
    array[n] = string_to_push;
    return array;
  }

  private static boolean inArray(String word, String[] array) {
    word = word.replaceAll(not_letters, "");
    for(String element : array) {
      String word_element = element.replaceAll(not_letters, "");
      if(word_element.equals(word)) return true;
    }
    return false;
  }
} 