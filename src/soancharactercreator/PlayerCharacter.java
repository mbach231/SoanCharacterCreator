/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soancharactercreator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import soancharactercreator.Settings.AttributeEn;

import soancharactercreator.Settings.RaceEn;
import soancharactercreator.Settings.CultureEn;

/**
 *
 *
 */
public class PlayerCharacter {

    private String name_;
    private RaceEn race_;
    private CultureEn culture_;

    private final Set<BaseSkill> skillSet_;
    private final Map<Header, Set<Skill>> headerMap_;

    private int cp_;
    private int maxCp_;

    private final int MAX_ATTRIBUTE_VALUE = 10;

    private int bp_;
    private int fp_;
    private int sp_;

    private int numPurchasesBp_;
    private int numPurchasesFp_;
    private int numPurchasesSp_;

    private AttributeEn humanExtraAttribute_;

    public PlayerCharacter(String name, String race, String culture) {

        name_ = name;
        race_ = RaceEn.valueOf(race);
        culture_ = CultureEn.valueOf(culture);

        skillSet_ = new HashSet();
        headerMap_ = new HashMap();

        cp_ = 0;
        maxCp_ = 20;

        bp_ = 2;
        fp_ = 2;
        sp_ = 2;

        numPurchasesBp_ = 0;
        numPurchasesFp_ = 0;
        numPurchasesSp_ = 0;

        humanExtraAttribute_ = null;

        setRace();
    }

    public boolean canPurchaseSkill(BaseSkill baseSkill) {

        if (baseSkill instanceof Header) {
            return baseSkill.getCost() <= cpRemaining();
        } else if (baseSkill instanceof Skill) {

            Skill skill = (Skill) baseSkill;

            if (skill.getCost() <= cpRemaining()) {
                if (skill.meetsSkillRequirements(this)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void purchaseSkill(BaseSkill baseSkill) {

        if (canPurchaseSkill(baseSkill)) {
            skillSet_.add(baseSkill);

            cp_ += baseSkill.getCost();

            if (baseSkill instanceof Skill) {
                Skill skill = (Skill) baseSkill;

                Header header = skill.getRequiredHeader();

                Set<Skill> headerSkillSet = headerMap_.containsKey(header) ? headerMap_.get(header) : new HashSet();
                headerSkillSet.add(skill);
                headerMap_.put(header, headerSkillSet);

            }
        }
    }

    public void removeSkill(BaseSkill baseSkill) {
        if (skillSet_.contains(baseSkill)) {

            skillSet_.remove(baseSkill);
            cp_ -= baseSkill.getCost();

            if (baseSkill instanceof Header) {

                Header header = (Header) baseSkill;

                if (headerMap_.containsKey(header)) {
                    for (Skill skill : headerMap_.get(header)) {
                        cp_ -= skill.getCost();
                        skillSet_.remove(skill);
                    }
                    headerMap_.remove(header);
                }

                purgeIllegalSkills();

            } else if (baseSkill instanceof Skill) {
                Skill skill = (Skill) baseSkill;

                Header header = skill.getRequiredHeader();

                Set<Skill> headerSkillSet = headerMap_.get(header);
                headerSkillSet.remove(skill);

                purgeIllegalSkills();

                //headerMap_.put(header, headerSkillSet);
            }

        }
    }

    private void purgeIllegalSkills() {

        Set<Skill> purgeSkills = new HashSet();

        while (true) {
            boolean removedSkills = false;

            for (Map.Entry<Header, Set<Skill>> headerEntry : headerMap_.entrySet()) {

                Set<Skill> headerSkills = headerEntry.getValue();

                for (Skill headerSkill : headerSkills) {
                    if (!headerSkill.meetsSkillRequirements(this)) {
                        cp_ -= headerSkill.getCost();

                        purgeSkills.add(headerSkill);
                        //headerSkills.remove(headerSkill);
                        removedSkills = true;
                    }
                }

                if (!purgeSkills.isEmpty()) {
                    for (Skill purgedSkill : purgeSkills) {
                        skillSet_.remove(purgedSkill);
                        headerSkills.remove(purgedSkill);
                    }
                }
                purgeSkills.clear();
                headerMap_.put(headerEntry.getKey(), headerSkills);
            }

            if (!removedSkills) {
                break;
            }
        }
    }

    public boolean hasSkill(BaseSkill skill) {
        return skillSet_.contains(skill);
    }

    public Set<BaseSkill> getKnownSkills() {
        return skillSet_;
    }

    public Set<Skill> getKnownHeaderSkills(Header header) {
        return headerMap_.get(header);
    }

    public int getBp() {
        return bp_;
    }

    public int getFp() {
        return fp_;
    }

    public int getSp() {
        return sp_;
    }

    public int getNumPurchasedBp() {
        return numPurchasesBp_;
    }

    public int getNumPurchasedFp() {
        return numPurchasesFp_;
    }

    public int getNumPurchasedSp() {
        return numPurchasesSp_;
    }

    public int getCp() {
        return cp_;
    }

    public int getMaxCp() {
        return maxCp_;
    }

    private int cpRemaining() {
        return maxCp_ - cp_;
    }

    public void increaseMaxCp(int incr) {
        maxCp_ += incr;
    }

    public void decreaseMaxCp(int decr) {
        maxCp_ -= decr;
    }

    public void setName(String name) {
        name_ = name;
    }

    public void setCulture(String name) {
        culture_ = CultureEn.valueOf(name);
    }

    public boolean increaseBp() {

        if (hasFreeHumanAttribute()) {
            humanExtraAttribute_ = AttributeEn.BP;
            bp_ += 1;
            return true;
        }

        if (bp_ < MAX_ATTRIBUTE_VALUE && numPurchasesBp_ < cpRemaining()) {
            numPurchasesBp_ += 1;
            cp_ += numPurchasesBp_;
            bp_ += 1;
            return true;
        }
        return false;
    }

    public boolean decreaseBp() {
        if (numPurchasesBp_ > 0) {
            cp_ -= numPurchasesBp_;
            numPurchasesBp_ -= 1;
            bp_ -= 1;
            return true;
        } else if (humanExtraAttribute_ == AttributeEn.BP) {
            humanExtraAttribute_ = null;
            bp_ -= 1;
            return true;
        }
        return false;
    }

    public boolean increaseFp() {

        if (hasFreeHumanAttribute()) {
            humanExtraAttribute_ = AttributeEn.FP;
            fp_ += 1;
            return true;
        }

        if (fp_ < MAX_ATTRIBUTE_VALUE && numPurchasesFp_ < cpRemaining()) {
            numPurchasesFp_ += 1;
            cp_ += numPurchasesFp_;
            fp_ += 1;
            return true;
        }
        return false;
    }

    public boolean decreaseFp() {
        if (numPurchasesFp_ > 0) {
            cp_ -= numPurchasesFp_;
            numPurchasesFp_ -= 1;
            fp_ -= 1;
            return true;
        } else if (humanExtraAttribute_ == AttributeEn.FP) {
            humanExtraAttribute_ = null;
            fp_ -= 1;
            return true;
        }
        return false;
    }

    public boolean increaseSp() {

        if (hasFreeHumanAttribute()) {
            humanExtraAttribute_ = AttributeEn.SP;
            sp_ += 1;
            return true;
        }

        if (sp_ < MAX_ATTRIBUTE_VALUE && numPurchasesSp_ < cpRemaining()) {
            numPurchasesSp_ += 1;
            cp_ += numPurchasesSp_;
            sp_ += 1;
            return true;
        }
        return false;
    }

    public boolean decreaseSp() {
        if (numPurchasesSp_ > 0) {
            cp_ -= numPurchasesSp_;
            numPurchasesSp_ -= 1;
            sp_ -= 1;
            return true;
        } else if (humanExtraAttribute_ == AttributeEn.SP) {
            humanExtraAttribute_ = null;
            sp_ -= 1;
            return true;
        }
        return false;
    }

    private void setRace() {

        Header freeHeader = null;

        switch (race_) {
            case Human:
                humanExtraAttribute_ = null;
                freeHeader = (Header) SkillLoader.getSkill("Influence");
                break;

            case Bastelm:
                sp_ += 2;
                freeHeader = (Header) SkillLoader.getSkill("Physicality");
                break;

            case Ethani:
                sp_ += 2;
                freeHeader = (Header) SkillLoader.getSkill("Cognizance");
                break;

            case Korahai:
                fp_ += 2;
                freeHeader = (Header) SkillLoader.getSkill("Physicality");
                break;

            case Rhavmani:
                fp_ += 2;
                freeHeader = (Header) SkillLoader.getSkill("Resonance");
                break;

            case Mulenti:
                sp_ += 2;
                freeHeader = (Header) SkillLoader.getSkill("Resonance");
                break;

            case Arisen:
                bp_ += 2;
                freeHeader = (Header) SkillLoader.getSkill("Resonance");
                break;

            case Homunculi:
                bp_ += 2;
                freeHeader = (Header) SkillLoader.getSkill("Cognizance");
                break;

        }

        if (freeHeader != null) {
            if (skillSet_.contains(freeHeader)) {
                cp_ -= freeHeader.getCost();
            } else {
                skillSet_.add(freeHeader);
            }
        }
    }

    private void removeRace() {

        Header freeHeader = null;

        switch (race_) {
            case Human:
                if (humanExtraAttribute_ != null) {

                    switch (humanExtraAttribute_) {
                        case BP:
                            bp_ -= 1;
                            break;
                        case FP:
                            fp_ -= 1;
                            break;
                        case SP:
                            sp_ -= 1;
                            break;
                    }
                    humanExtraAttribute_ = null;
                }
                freeHeader = (Header) SkillLoader.getSkill("Influence");
                break;

            case Bastelm:
                sp_ -= 2;
                freeHeader = (Header) SkillLoader.getSkill("Physicality");
                break;

            case Ethani:
                sp_ -= 2;
                freeHeader = (Header) SkillLoader.getSkill("Cognizance");
                break;

            case Korahai:
                fp_ -= 2;
                freeHeader = (Header) SkillLoader.getSkill("Physicality");
                break;

            case Rhavmani:
                fp_ -= 2;
                freeHeader = (Header) SkillLoader.getSkill("Resonance");
                break;

            case Mulenti:
                sp_ -= 2;
                freeHeader = (Header) SkillLoader.getSkill("Resonance");
                break;

            case Arisen:
                bp_ -= 2;
                freeHeader = (Header) SkillLoader.getSkill("Resonance");
                break;

            case Homunculi:
                bp_ -= 2;
                freeHeader = (Header) SkillLoader.getSkill("Cognizance");
                break;

        }

        skillSet_.remove(freeHeader);

        if (headerMap_.containsKey(freeHeader)) {
            for (Skill skill : headerMap_.get(freeHeader)) {
                skillSet_.remove(skill);
                cp_ -= skill.getCost();
            }
            headerMap_.remove(freeHeader);
        }
    }

    public Header getFreeRacialHeader() {
        switch (race_) {
            case Human:
                return (Header) SkillLoader.getSkill("Influence");

            case Bastelm:
                return (Header) SkillLoader.getSkill("Physicality");

            case Ethani:
                return (Header) SkillLoader.getSkill("Cognizance");

            case Korahai:
                return (Header) SkillLoader.getSkill("Physicality");

            case Rhavmani:
                return (Header) SkillLoader.getSkill("Resonance");

            case Mulenti:
                return (Header) SkillLoader.getSkill("Resonance");

            case Arisen:
                return (Header) SkillLoader.getSkill("Resonance");

            case Homunculi:
                return (Header) SkillLoader.getSkill("Cognizance");
            default:
                return null;
        }
    }

    public void setRace(String race) {

        removeRace();
        race_ = RaceEn.valueOf(race);
        setRace();

        purgeIllegalSkills();

    }

    public RaceEn getRace() {
        return race_;
    }

    public boolean hasFreeHumanAttribute() {
        return race_.equals(RaceEn.Human) && humanExtraAttribute_ == null;
    }

    public boolean freeHumanAttributeSetToAttribute(AttributeEn attribute) {

        return humanExtraAttribute_ == null ? false : humanExtraAttribute_.equals(attribute);
    }

}
