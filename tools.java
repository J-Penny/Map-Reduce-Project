import java.io.*;
import java.util.ArrayList;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Scanner;

public class tools {
  public static void main(String[] args) throws IOException{
    try {
      URL url = new URL("http://www.gutenberg.org/files/1342/1342-0.txt");
      Scanner s = new Scanner(url.openStream());
      String text = "";
      while(s.hasNext()) {
        text += s.nextLine() + " ";
      }
      s.close();
      Collection anagrams = anagram_finder(text).values();
      print_anagrams(anagrams);
    }
    catch(IOException ex) {
      // there was some connection problem, or the file did not exist on the server,
      // or URL was not in the right format.
      ex.printStackTrace();
    }
  }

  private static Hashtable anagram_finder(String text) {
    String[] words = get_words(text);
    Hashtable<String, String[]> anagrams = new Hashtable<String, String[]>();
    for(int i = 0; i < words.length; i++) {
      if(words[i].length() < 2) continue;
      String ordered_word = format_word(words[i]);
      String[] anagramList = anagrams.get(ordered_word);
      boolean isAnagram = anagrams.containsKey(ordered_word);
      if(isAnagram && inArray(words[i], anagramList)) continue;
      if(isAnagram){
        anagramList = push(anagramList, words[i]);
        anagrams.put(ordered_word, anagramList);
      } else {
        String[] anagram = {words[i]};
        anagrams.put(ordered_word, anagram);
      }
    }
    return anagrams;
  }

  private static void print_anagrams(Collection coll){
    for(int i = 0; i < coll.size(); i++){
      String[] anagrams = (String[]) coll.toArray()[i];
      if(anagrams.length > 1)
        System.out.print(Arrays.toString(anagrams));
    }
  }

  private static String[] get_words(String text) {
    text = text.replaceAll("(?<= )'|[^a-zA-Z- '_]","");
    text = text.toLowerCase();
    String[] words = text.split("  |[ _]|--");
    return words;
  }

  private static String format_word(String word) { 
    word = word.replaceAll("[^a-z]", "");
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
    word = word.replaceAll("[^a-z]", "");
    for(String element : array) {
      String word_element = element.replaceAll("[^a-z]", "");
      if(word_element.equals(word)) return true;
    }
    return false;
  }
}