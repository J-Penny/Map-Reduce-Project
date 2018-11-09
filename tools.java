import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;

public class tools {
  public static void main(String[] args) throws IOException{
    File file = new File("happy.txt");
    BufferedReader br = new BufferedReader(new FileReader(file));
    String happy = br.readLine();
    br.close();
    Collection anagrams = anagram_finder(happy).values();
    collection_to_array(anagrams);
  }

  private static Hashtable anagram_finder(String text) {
    String[] words = get_words(text);
    Hashtable<String, String[]> anagrams = new Hashtable<String, String[]>();
    for(int i = 0; i < words.length; i++) {
      String ordered_word = format_word(words[i]);
      String[] anagramList = anagrams.get(ordered_word);
      if(anagrams.containsKey(ordered_word) && !Arrays.asList(anagramList).contains(words[i])){
        anagramList = push(anagramList, words[i]);
        anagrams.put(ordered_word, anagramList);
      } else {
        String[] anagram = {words[i]};
        anagrams.put(ordered_word, anagram);
      }
    }
    return anagrams;
  }

  private static void collection_to_array(Collection coll){
    for(int i = 0; i < coll.size(); i++){
      String[] anagrams = (String[]) coll.toArray()[i];
      if(anagrams.length > 1)
        System.out.print(Arrays.toString(anagrams));
    }
  }

  private static String[] get_words(String text) {
    text = text.replaceAll("[,.?!]|\"", "");
    text = text.toLowerCase();
    String[] words = text.split("  |[ _]|--");
    return words;
  }

  private static String format_word(String word) { 
    word = word.replaceAll("[^a-zA-Z]", "");
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
}