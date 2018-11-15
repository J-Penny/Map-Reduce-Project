import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Scanner;

public class Tools {
  private static String not_letters = "[^a-zA-Z\u00C0-\u017F]";
  private static String not_word_chars = "(?<= )'|[^a-zA-Z\u00C0-\u017F '-]";
  public static void main(String[] args) throws IOException{
    try {
      URL url = new URL("http://www.gutenberg.org/files/46/46-0.txt");
      Scanner s = new Scanner(url.openStream());
      String text = "";
      while(s.hasNext()) {
        text += s.nextLine() + " ";
      }
      s.close();
      Collection anagrams = anagram_finder(text).values();
      print_anagrams(anagrams);
      System.out.println("Words: " + get_words(text).length);
    }
    catch(IOException ex) {
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
    int count = 0;
    for(int i = 0; i < coll.size(); i++){
      String[] anagrams = (String[]) coll.toArray()[i];

      if(anagrams.length > 1) {
        count++;
        System.out.println(Arrays.toString(anagrams));}
    }
    System.out.println("Anagrams: " + count);
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