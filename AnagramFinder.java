import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.util.Arrays;

public class AnagramFinder {

	//Regex to format words
	private static String not_letters = "[^a-zA-Z\u00C0-\u017F]";
	private static String not_word_chars = "(?<= )'|[^a-zA-Z\u00C0-\u017F '-]";
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "anagram finder");
		job.setJarByClass(AnagramFinder.class);
		job.setMapperClass(AMapper.class);
		job.setReducerClass(AReducer.class);
		job.setOutputKeyClass(Text.class); //formated_word
		job.setOutputValueClass(Text.class); //anagrams array
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	//<KEYIN, VALUEIN (input text), KEYOUT(formated_word), VALUEOUT(fullword)>
	public static class AMapper extends Mapper<Object, Text, Text, Text>{
		private Text formated_word = new Text();
		private Text full_word = new Text();

		public void map(Object key, Text value, Context context)
		 throws IOException, InterruptedException {
			String[] words = get_words(value.toString()); //Get all the words from the string

			for(String word : words) { //for every word
				formated_word.set(new Text(format_word(word))); //store a formated word
				full_word.set(new Text(word)); //store the full word
				context.write(formated_word, full_word); // formated_word(key) -> full_word(value)
			}
		}
	}

	//<KEYIN(formated_word), VALUEIN(full_word), KEYOUT(formated_word), VALUEOUT(anagram array as text)>
	public static class AReducer extends Reducer<Text, Text, NullWritable, Text> {
		private Text anagram_text;

		//formated_word -> {full_word, full_word, ...} with possible duplicates
		public void reduce(Text key, Iterable<Text> values, Context context)
		 throws IOException, InterruptedException {
			String[] anagrams = new String[0];

			for(Text val : values) { //for every value in a given key(formated_word)
				if(inArray(val.toString(), anagrams)) continue; //skip if word already stored
				anagrams = push(anagrams, val.toString()); //otherwise add word to anagrams
			}
			if(anagrams.length > 1) { //if more than 1 value to the key
				anagram_text = new Text(Arrays.toString(anagrams)); //Convert anagram array to text type
				NullWritable null_key = NullWritable.get();
				context.write(null_key, anagram_text); //[full_word, full_word, ...]
			}
		}
	}

	//Split a long string into words
  private static String[] get_words(String text) {
    text = text.toLowerCase();
    text = text.replaceAll(not_word_chars,"");
    String[] words = text.split(" +|--");
    return words;
  }

	//alphabetise and remove non letter characters
  private static String format_word(String word) { 
    word = word.replaceAll(not_letters, "");
    return alphabetise(word);
  }

	//alphabetise a word
  private static String alphabetise(String input) {
    char[] word = input.toLowerCase().toCharArray();
    Arrays.sort(word);
    return new String(word);
  }

	//Add an element on the end of an array
  private static String[] push(String[] array, String element){
    final int n = array.length;
    array = Arrays.copyOf(array, n + 1);
    array[n] = element;
    return array;
  }

	//Is the element in the array?
  private static boolean inArray(String word, String[] array) {
		word = word.replaceAll(not_letters, "");
		if(array.length < 1) return false;
    for(String element : array) {
      String word_element = element.replaceAll(not_letters, "");
      if(word_element.equals(word)) return true;
    }
    return false;
  }
} 