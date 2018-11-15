import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.util.Arrays;

public class AnagramFinder {
	private static String not_letters = "[^a-zA-Z\u00C0-\u017F]";
  private static String not_word_chars = "(?<= )'|[^a-zA-Z\u00C0-\u017F '-]";
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "anagram finder");
		job.setJarByClass(AnagramFinder.class);
		job.setMapperClass(AMapper.class);
		job.setReducerClass(AReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(String[].class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	public static class AMapper extends Mapper<Object, Text, Text, String>{
		private Text formated_word = new Text();
		private Text full_word = new Text();

		public void map(Object key, Text value, Context context)
		 throws IOException, InterruptedException {
			String[] words = get_words(value.toString());

			for(String word : words) {
				formated_word.set(format_word(word));
				full_word.set(word);
				context.write(formated_word, full_word);
			}
		}
	}

	public static class AReducer extends Reducer<Text, ArrayWritable, Text, ArrayWritable> {
		private TextArrayWritable result;
		
		public void reduce(Text key, Iterable<Text> values, Context context)
		 throws IOException, InterruptedException {
			String[] anagrams = new String[0];
			for(String val : values) {
				if(inArray(val, anagrams)) continue;
				anagrams = push(anagrams, val);
			}
			if(anagrams.length > 1) {
				result = anagrams;
				context.write(key, result);
			}
		}
	}

	public static class TextArrayWritable extends ArrayWritable {
		public TextArrayWritable() {
				super(Text.class);
		}

		public TextArrayWritable(String[] strings) {
				super(Text.class);
				Text[] texts = new Text[strings.length];
				for (int i = 0; i < strings.length; i++) {
						texts[i] = new Text(strings[i]);
				}
				set(texts);
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