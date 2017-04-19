/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soancharactercreator;

import java.util.Map;
import soancharactercreator.gui.*;

/**
 *
 * @author Mike
 */
public class SoanCharacterCreator {

    Map<String, BaseSkill> skillMap_;

    enum EntryType {

        HEADER,
        SKILL,
        SKILL_GROUP
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SkillLoader skillLoader = new SkillLoader("soanSkills.json");

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
                //new SinsApplet().setVisible(true);
            }
        });
    }

}
