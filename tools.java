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
    text = text.replaceAll("[,.?!]|\"", " ");
    text = text.toLowerCase();
    String[] words = text.split("  |[ _]|--");
    Hashtable<String, String[]> anagrams = new Hashtable<String, String[]>();
    for(int i = 0; i < words.length; i++) {
      String word = words[i].replaceAll("[^a-zA-Z]", "");
      String ordered_word = alphabetise(word);
      String[] anagramList = anagrams.get(ordered_word);
      if(anagrams.containsKey(ordered_word) && !Arrays.asList(anagramList).contains(words[i])){
        anagrams.put(ordered_word, push(anagrams.get(ordered_word), words[i]));
      } else {
        String[] anagram = {words[i]};
        anagrams.put(ordered_word, anagram);
      }
    }
    return anagrams;
  }

  private static void collection_to_array(Collection coll){
    for(Object obj : coll){
      String[] anagrams = (String[]) obj;
      if(anagrams.length > 1)
        System.out.print(Arrays.toString(anagrams));
    }
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