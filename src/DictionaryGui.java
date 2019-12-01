/*********************************************************************************
 *Student ID: 686274
 *Student Name: Ziping Gao
 *Last Modified: 04/09/2019
 *Description: The DictionaryGui class is the user interface for the client side 
 *dictionary. Users are able to add words, get meanings and delete words. 
 *********************************************************************************/

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DictionaryGui {
    // main window
    public DictionaryGui(){
        JFrame frame = new JFrame();
        // layout & panel
        JPanel panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // search field
        JTextField searchText = new JTextField("", 20);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.insets.set(10, 10, 10, 10);
        panel.add(searchText, constraints);

        // search button
        JButton searchButton = new JButton("Search");
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.insets.set(10, 10, 10, 10);
        panel.add(searchButton, constraints);

        // display field
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Meaning");
        JTable meaningsTable = new JTable(model);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridheight = 3;
        constraints.insets.set(10, 10, 10, 10);
        JScrollPane meaningsPane = new JScrollPane(meaningsTable);
        panel.add(meaningsPane, constraints);

        // add button
        JButton addButton = new JButton("Add");
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridheight = 1;
        constraints.insets.set(10, 10, 10, 10);
        panel.add(addButton, constraints);

        // delete button
        JButton deleteButton = new JButton("Delete");
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridheight = 1;
        constraints.insets.set(10, 10, 10, 10);
        panel.add(deleteButton, constraints);

        // event listener
        searchButton.addActionListener(e -> {
            String text = searchText.getText();
            if(text == null || text.equals("")) 
            	{
            		showMessage("Cannot search an empty word");
            		return;
            	}

            // create threads to send "search dictionary" request and receive the response
            Runnable run = () -> {
                try {
                    get(text, meaningsTable);
                } catch (IOException ex) {
                	showMessage("Server Connection Error");
                    ex.printStackTrace();
                }
            };
            Thread thread = new Thread(run);
            thread.start();
        });
        addButton.addActionListener(e -> {
            // create the pop-up window for "add" function
            DictionaryModel dictionary = new DictionaryModel();
            new DictionaryGui(dictionary);
        });
        deleteButton.addActionListener(e -> {
            String text = searchText.getText();
            if(text == null || text.equals("")) 
            	{
            		showMessage("Cannot delete a non-existing word.");
            		return;
            	}

            // create threads to send "delete dictionary" request and receive the processed response
            Runnable run = () -> {
                try{
                    RequestMessage<String> request = new RequestMessage<>();
                    request.setData(text);
                    request.setAction(ServerAction.Delete);

                    ObjectMapper mapper = new ObjectMapper();
                    String responseJson = DictionaryClient.send(request);
                    JsonNode node = mapper.readValue(responseJson, JsonNode.class);
                    ResponseMessage<String> response = mapper.convertValue(node, new TypeReference<ResponseMessage<String>>(){});

                    // receive response message and re-display the current displaying meanings
                    if(response.getSuccess()){
                        showMessage("Success");
                        get(text, meaningsTable);
                    }else{
                        showMessage("Cannot find the word");
                        removeAllMeanings(meaningsTable);
                    }
                } catch (IOException ex) {
                	showMessage("Server Connection Error");
                    ex.printStackTrace();
                }
            };
            Thread thread = new Thread(run);
            thread.start();
        });

        // finalize
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    // add window
    public DictionaryGui(DictionaryModel dict){
        JFrame frame = new JFrame();
        // layout & panel
        JPanel panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // meaning section
        JPanel meaningPanel = new JPanel();
        GridBagLayout meaningLayout = new GridBagLayout();
        meaningPanel.setLayout(meaningLayout);
        GridBagConstraints meaningCons = new GridBagConstraints();
        meaningCons.fill = GridBagConstraints.HORIZONTAL;

        // word label
        JLabel wordLabel = new JLabel("Word:");
        meaningCons.gridx = 0;
        meaningCons.gridy = 0;
        meaningCons.insets.set(10, 10, 10, 10);
        meaningCons.anchor = GridBagConstraints.WEST;
        meaningPanel.add(wordLabel, meaningCons);

        // word input
        JTextField wordText = new JTextField("", 20);
        meaningCons.gridx = 1;
        meaningCons.gridy = 0;
        meaningCons.insets.set(10, 10, 10, 10);
        meaningPanel.add(wordText, meaningCons);

        // meaning label
        JLabel meaningLabel = new JLabel("Meanings:");
        meaningCons.gridx = 0;
        meaningCons.gridy = 1;
        meaningCons.insets.set(10, 0, 10, 10);
        meaningCons.anchor = GridBagConstraints.WEST;
        meaningPanel.add(meaningLabel, meaningCons);

        // meaning input
        List<JTextField> meaningTextList = new ArrayList<>();
        JTextField meaningText = new JTextField("", 20);
        meaningCons.gridx = 1;
        meaningCons.gridy = 1;
        meaningCons.insets.set(10 ,10 ,10 ,10);
        meaningTextList.add(meaningText);
        meaningPanel.add(meaningText, meaningCons);

        // add meaning button
        JButton addMeaningButton = new JButton("Add");
        meaningCons.gridx = 2;
        meaningCons.gridy = 1;
        meaningCons.insets.set(10, 10, 10 ,10);
        meaningPanel.add(addMeaningButton, meaningCons);

        // event listener
        addMeaningButton.addActionListener(e -> {
            // display multiple meanings in the input field (default as one)
            JTextField meaningText12 = new JTextField("", 20);
            meaningCons.gridx = 1;
            meaningCons.gridy = meaningTextList.size() + 1;
            meaningCons.insets.set(10, 10, 10, 10);
            meaningPanel.add(meaningText12, meaningCons);

            // allow deleting newly added meanings
            JButton removeButton = new JButton("Remove");
            meaningCons.gridx = 2;
            meaningCons.gridy = meaningTextList.size() + 1;
            meaningCons.insets.set(10, 10, 10, 10);
            meaningPanel.add(removeButton, meaningCons);

            removeButton.addActionListener(e1 -> {
                meaningPanel.remove(meaningText12);
                meaningPanel.remove(removeButton);

                // while deleting meaning, refresh panel
                meaningTextList.remove(meaningText12);
                meaningPanel.revalidate();
                meaningPanel.repaint();
            });
            // while adding new meaning, refresh panel 
            meaningTextList.add(meaningText12);
            meaningPanel.revalidate();
            meaningPanel.repaint();
        });

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.anchor = GridBagConstraints.SOUTHWEST;
        panel.add(meaningPanel, constraints);

        // submit button
        JButton submitButton = new JButton("Submit");
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.insets.set(10, 10, 10 ,10);
        panel.add(submitButton, constraints);

        // event listener
        submitButton.addActionListener(e -> {
            // get input value
            String word = wordText.getText();
            if(word == null || word.equals("")) {
            	showMessage("Cannot add an empty word");
            	return;
            }

            List<String> meanings = new ArrayList<String>();
            for(JTextField meaningText1 : meaningTextList){
                String meaning = meaningText1.getText();
                if(meaning == null || meaning.equals("")) continue;
                meanings.add(meaning);
            }
            if(meanings.size() == 0) {
            	showMessage("Cannot add a word without meaning.");
            	return;
            }

            dict.setVocabulary(word);
            dict.setMeaning(meanings);
            // create threads to send "add dictionary" request and receive processed response
            Runnable run = () -> {
                try{
                    RequestMessage<DictionaryModel> request = new RequestMessage<>();
                    request.setAction(ServerAction.Add);
                    request.setData(dict);

                    ObjectMapper mapper = new ObjectMapper();
                    String responseJson = DictionaryClient.send(request);                    
                    JsonNode node = mapper.readValue(responseJson, JsonNode.class);
                    ResponseMessage<String> response = mapper.convertValue(node, new TypeReference<ResponseMessage<String>>(){});
                    // receive response message and show to users
                    if(response.getSuccess()){
                        showMessage(response.getData());
                    }else{
                        showMessage(response.getData());
                    }
                } catch (IOException ex) {
                	showMessage("Server Connection Error");
                    ex.printStackTrace();
                }
            };
            Thread thread = new Thread(run);
            thread.start();
            // close "add" function window
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        });

        // finalize
        frame.add(panel);
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private void showMessage(String message){
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, message);
    }

    private void get(String text, JTable table) throws IOException {
        RequestMessage<String> request = new RequestMessage<>();
        request.setAction(ServerAction.Get);
        request.setData(text);

        ObjectMapper mapper = new ObjectMapper();
        String responseJson = DictionaryClient.send(request);
        JsonNode node = mapper.readValue(responseJson, JsonNode.class);
        ResponseMessage<DictionaryModel> response = mapper.convertValue(node, new TypeReference<ResponseMessage<DictionaryModel>>(){});

        // table for showoing meanings and update according to updated meanings received
//        if(response.getSuccess()){
            removeAllMeanings(table);
            showMeanings(table, response.getData());
//        }else{
//            removeAllMeanings(table);
//        }
    }

    private void removeAllMeanings(JTable table){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        while(true){
            int count = model.getRowCount();
            if(count == 0) break;
            model.removeRow(count - 1);
        }
    }

    private void showMeanings(JTable table, DictionaryModel dict){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for(String meaning : dict.getMeaning()){
            model.addRow(new Object[]{meaning});
        }
    }
}
