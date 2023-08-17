import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
class Word {
    String word_target; // từ mới
    String word_explain; // giải nghĩa
}

class Dictionary {
    Word[] words; // mảng lưu trữ Word
    int count; // số lượng từ vựng hiện có trong từ điển

    Dictionary() {
        words = new Word[1000];
        count = 0;
    }
}

class DictionaryManagement {
    void insertFromCommandline(Dictionary dict) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhap so luong tu: ");
        int n = scanner.nextInt();
        scanner.nextLine(); // consume the newline character

        for (int i = 0; i < n; i++) {
            Word word = new Word();
            System.out.print("Nhap tu tieng Anh: ");
            word.word_target = scanner.nextLine();
            System.out.print("Nhap giai thich sang tieng Viet: ");
            word.word_explain = scanner.nextLine();
            dict.words[dict.count] = word;
            dict.count++;
        }
    }

    void insertFromFile(Dictionary dict, String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                Word word = new Word();
                word.word_target = parts[0];
                word.word_explain = parts[1];
                dict.words[dict.count] = word;
                dict.count++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String dictionaryLookup(Dictionary dict, String lookupWord) {
        for (int i = 0; i < dict.count; i++) {
            if (dict.words[i].word_target.equalsIgnoreCase(lookupWord)) {
                return dict.words[i].word_explain;
            }
        }
        return "Word not found.";
    }

    void dictionaryAddWord(Dictionary dict, String target, String explain) {
        Word word = new Word();
        word.word_target = target;
        word.word_explain = explain;
        dict.words[dict.count] = word;
        dict.count++;
    }

    void dictionaryEditWord(Dictionary dict, String target, String newExplain) {
        for (int i = 0; i < dict.count; i++) {
            if (dict.words[i].word_target.equalsIgnoreCase(target)) {
                dict.words[i].word_explain = newExplain;
                break;
            }
        }
    }

    void dictionaryDeleteWord(Dictionary dict, String target) {
        for (int i = 0; i < dict.count; i++) {
            if (dict.words[i].word_target.equalsIgnoreCase(target)) {
                for (int j = i; j < dict.count - 1; j++) {
                    dict.words[j] = dict.words[j + 1];
                }
                dict.count--;
                break;
            }
        }
    }

    String[] dictionarySearcher(Dictionary dict, String prefix) {
        int matches = 0;
        for (int i = 0; i < dict.count; i++) {
            if (dict.words[i].word_target.toLowerCase().startsWith(prefix.toLowerCase())) {
                matches++;
            }
        }

        String[] results = new String[matches];
        int index = 0;
        for (int i = 0; i < dict.count; i++) {
            if (dict.words[i].word_target.toLowerCase().startsWith(prefix.toLowerCase())) {
                results[index] = dict.words[i].word_target;
                index++;
            }
        }
        return results;
    }

    void dictionaryExportToFile(Dictionary dict, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            for (int i = 0; i < dict.count; i++) {
                writer.write(dict.words[i].word_target + "\t" + dict.words[i].word_explain);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class DictionaryCommandline {
    void showAllWords(Dictionary dict) {
        System.out.println("No | English | Vietnamese");
        for (int i = 0; i < dict.count; i++) {
            System.out.println((i + 1) + " | " + dict.words[i].word_target + " | " + dict.words[i].word_explain);
        }
    }

    void dictionaryAdvanced() {
        Dictionary dict = new Dictionary();
        DictionaryManagement dm = new DictionaryManagement();
        dm.insertFromFile(dict, "dictionaries.txt");
        showAllWords(dict);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhap tu tieng Anh can tra cuu: ");
        String lookupWord = scanner.nextLine();
        String explanation = dm.dictionaryLookup(dict, lookupWord);
        System.out.println("Nghia cua tu: " + explanation);

        System.out.print("Nhap mot so ky tu dau tien cua tu tieng Anh: ");
        String prefix = scanner.nextLine();
        String[] searchResults = dm.dictionarySearcher(dict, prefix);
        System.out.println("Cac tu bat dau bang " + prefix + ":");
        for (int i = 0; i < searchResults.length; i++) {
            System.out.println(searchResults[i]);
        }
	
	  // Thêm từ vào từ điển
        System.out.print("Nhap tu tieng Anh can them: ");
        String addWord = scanner.nextLine();
        System.out.print("Nhap nghia cua tu trong tieng Viet: ");
        String addExplain = scanner.nextLine();
        dm.dictionaryAddWord(dict, addWord, addExplain);


        System.out.print("Nhap tu tieng Anh can xoa: ");
        String deleteWord = scanner.nextLine();
        dm.dictionaryDeleteWord(dict, deleteWord);

        // Sửa nghĩa của từ trong từ điển
        System.out.print("Nhap tu tieng Anh can sua: ");
        String editWord = scanner.nextLine();
        System.out.print("Nhap nghia cua tu trong tieng Viet: ");
        String newExplain = scanner.nextLine();
        dm.dictionaryEditWord(dict, editWord, newExplain);
	// Hien thi cac tu sau thay doi
        showAllWords(dict);
        dm.dictionaryExportToFile(dict, "dictionaries.txt");
        System.out.print("Nhap ten file export: ");
        String filename = scanner.nextLine();
        dm.dictionaryExportToFile(dict, "filename");
    }
}

class DictionaryApplication {
    JFrame frame;
    JList<String> wordList;
    JTextArea explanationTextArea;
    Dictionary dict;

    public DictionaryApplication() {
        dict = new Dictionary();
        DictionaryManagement dm = new DictionaryManagement();
        dm.insertFromFile(dict, "dictionaries.txt");

        frame = new JFrame("English-Vietnamese Dictionary");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        wordList = new JList<>(getWordListData());
        JScrollPane wordListScrollPane = new JScrollPane(wordList);
        wordList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selectedIndex = wordList.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < dict.count) {
                    explanationTextArea.setText(dict.words[selectedIndex].word_explain);
                }
            }
        });

        explanationTextArea = new JTextArea();
        explanationTextArea.setLineWrap(true);
        JScrollPane explanationScrollPane = new JScrollPane(explanationTextArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, wordListScrollPane, explanationScrollPane);
        splitPane.setResizeWeight(0.3);
        frame.add(splitPane);

    }

    private String[] getWordListData() {
        String[] data = new String[dict.count];
        for (int i = 0; i < dict.count; i++) {
            data[i] = dict.words[i].word_target;
        }
        return data;
    }

    public void runApplication() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {               
		frame.setVisible(true);
            }
        });
    }
}
public class DictionaryV4 {
    public static void main(String[] args) {
       DictionaryApplication app = new DictionaryApplication();
        app.runApplication();
    }
}

