package soancharactercreator;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import soancharactercreator.requirements.HeaderRequirement;
import soancharactercreator.requirements.Requirements;
import soancharactercreator.requirements.SkillRequirement;
import soancharactercreator.requirements.TierRequirement;

/**
 *
 *
 */
public class SkillLoader {

    private static String path_;
    private static Map<String, BaseSkill> skillMap_;
    private static Map<Header, List<Skill>> headerMap_;
    private static JSONArray jsonArray_;

    private static List<String> skillNames_;
    private static List<String> headerNames_;
    private static List<String> skillGroupNames_;

    private static List<Skill> skills_;
    private static List<Header> headers_;
    private static Map<String, Set<BaseSkill>> skillGroupMap_;

    enum EntryType {

        HEADER,
        SKILL,
        SKILL_GROUP
    }

    public SkillLoader(String path) {

        path_ = path;

        skillMap_ = new HashMap();
        headerMap_ = new HashMap();
        skillGroupMap_ = new HashMap();

        skillNames_ = new ArrayList();
        headerNames_ = new ArrayList();
        skillGroupNames_ = new ArrayList();

        skills_ = new ArrayList();
        headers_ = new ArrayList();
        skillGroupMap_ = new HashMap();

        try {
            JSONParser parser = new JSONParser();
            jsonArray_ = (JSONArray) parser.parse(new FileReader(path_));
        } catch (Exception ex) {
            Logger.getLogger(SoanCharacterCreator.class.getName()).log(Level.SEVERE, null, ex);
        }

        loadHeaders();
        loadSkillGroups();
        loadSkills();

    }

    public static List<Header> getHeaderList() {
        return headers_;
    }

    public static BaseSkill getSkill(String name) {
        return skillMap_.get(name);
    }

    public static List<Skill> getHeaderSkills(Header header) {
        return headerMap_.get(header);
    }

    public Map<Header, List<Skill>> getHeaderMap() {
        return headerMap_;
    }

    private boolean skillLoaded(String name) {
        return skillMap_.containsKey(name);
    }

    private void loadHeaders() {

        for (Object object : jsonArray_) {
            JSONObject obj = (JSONObject) object;

            EntryType type = EntryType.valueOf((String) obj.get("type"));

            if (type.equals(EntryType.HEADER)) {

                String name = (String) obj.get("name");
                int cost = ((Long) obj.get("cost")).intValue();
                Header header = new Header(name, cost);
                headers_.add(header);
                addSkill(name, header);
            }
        }
    }

    private void loadSkillGroups() {

        for (Object object : jsonArray_) {
            JSONObject obj = (JSONObject) object;

            EntryType type = EntryType.valueOf((String) obj.get("type"));

            if (type.equals(EntryType.SKILL_GROUP)) {

                String name = (String) obj.get("name");

                JSONArray skills = (JSONArray) obj.get("skills");
                Set<BaseSkill> skillSet = new HashSet();

                for (Object skillNameObj : skills) {
                    String skillName = (String) skillNameObj;

                    if (skillMap_.containsKey(skillName)) {
                        skillSet.add(skillMap_.get(skillName));
                    } else {
                        BaseSkill skill = load(skillName);
                        if (skill != null) {
                            skillSet.add(skill);
                        }
                    }
                }

                skillGroupMap_.put(name, skillSet);
            }
        }
    }

    private void loadSkills() {

        for (Object object : jsonArray_) {
            JSONObject obj = (JSONObject) object;

            String name = (String) obj.get("name");
            EntryType type = EntryType.valueOf((String) obj.get("type"));

            if (type.equals(EntryType.SKILL)) {
                Skill skill = loadSkill(obj);
                if (skill != null) {
                    addSkill(name, skill);
                }
            }
        }
    }

    private Skill loadSkill(JSONObject obj) {
        String name = (String) obj.get("name");

        if (skillMap_.containsKey(name)) {
            //return (Skill) skillMap_.get(name);
        }

        Integer cost = ((Long) obj.get("cost")).intValue();
        String activationCost = (String) obj.get("activation_cost");
        Integer tierRank = obj.get("tier_rank") != null ? ((Long) obj.get("tier_rank")).intValue() : null;
        String tierType = (String) obj.get("tier_type");
        String requiredHeader = (String) obj.get("required_header");
        JSONArray requiredSkills = (JSONArray) obj.get("required_skills");
        JSONObject requiredTier = (JSONObject) obj.get("required_tier");

        HeaderRequirement headerRequirement = null;
        SkillRequirement skillRequirement = null;
        TierRequirement tierRequirement = null;

        if (requiredHeader != null) {

            Header header = getHeader(requiredHeader);

            if (header != null) {
                headerRequirement = new HeaderRequirement(header);
            } else {
                System.out.println("Failed to find required header (" + requiredHeader + ") for skill (" + name + ")");
                return null;
            }
        }

        if (requiredSkills != null) {
            Set<Set<BaseSkill>> requiredSkillSet = new HashSet();
            for (Object skillSetObj : requiredSkills) {

                Set<BaseSkill> skillSet = new HashSet();

                if (skillSetObj instanceof String) {

                    String requiredSkillName = (String) skillSetObj;

                    if (skillGroupMap_.containsKey(requiredSkillName)) {
                        for (BaseSkill skill : skillGroupMap_.get(requiredSkillName)) {
                            skillSet.add(skill);
                        }
                    } else if (skillMap_.containsKey(requiredSkillName)) {
                        skillSet.add(skillMap_.get(requiredSkillName));
                    } else {
                        BaseSkill requiredSkill = load(requiredSkillName);
                        if (requiredSkill != null) {
                            skillSet.add(requiredSkill);
                        } else {
                            System.out.println("Failed to find required skill (" + requiredSkillName + ") for skill (" + name + ")");
                            return null;
                        }
                    }
                } else if (skillSetObj instanceof JSONArray) {
                    JSONArray skillSetArray = (JSONArray) skillSetObj;

                    for (Object skillNameObj : skillSetArray) {

                        String requiredSkillName = (String) skillNameObj;
                        if (skillGroupMap_.containsKey(requiredSkillName)) {
                            for (BaseSkill skill : skillGroupMap_.get(requiredSkillName)) {
                                skillSet.add(skill);
                            }
                        } else if (skillMap_.containsKey(requiredSkillName)) {
                            BaseSkill baseSkill = skillMap_.get(requiredSkillName);

                            if (baseSkill.getType().equals(EntryType.SKILL)) {
                                skillSet.add(baseSkill);
                            }
                        } else {
                            BaseSkill requiredSkill = load(requiredSkillName);
                            if (requiredSkill != null) {
                                skillSet.add(requiredSkill);
                            } else {
                                System.out.println("Failed to find required skill (" + requiredSkillName + ") for skill (" + name + ")");
                                return null;
                            }
                        }
                    }

                }

                if (!skillSet.isEmpty()) {
                    requiredSkillSet.add(skillSet);
                }
            }

            if (!requiredSkillSet.isEmpty()) {
                skillRequirement = new SkillRequirement(requiredSkillSet);
            }
        }

        if (requiredTier != null) {
            Header requiredTierHeader = getHeader((String) requiredTier.get("header"));
            Integer requiredTierRank = requiredTier.get("tier_rank") != null ? ((Long) requiredTier.get("tier_rank")).intValue() : null;
            String requiredTierType = (String) requiredTier.get("tier_type");
            Integer numRequired = requiredTier.get("num_required") != null ? ((Long) requiredTier.get("num_required")).intValue() : null;

            if (requiredTierHeader != null
                    && numRequired != null) {
                tierRequirement = new TierRequirement(requiredTierHeader, requiredTierType, requiredTierRank, numRequired);
            } else {
                System.out.println("Failed to load tier requirements for (" + name + ")");
                return null;
            }
        }

        Requirements requirements = new Requirements(headerRequirement, skillRequirement, tierRequirement);

        return new Skill(name, cost, activationCost, tierRank, tierType, requirements);
    }

    private Header loadHeader(JSONObject obj) {

        EntryType type = EntryType.valueOf((String) obj.get("type"));

        if (type.equals(EntryType.HEADER)) {

            String name = (String) obj.get("name");
            int cost = ((Long) obj.get("cost")).intValue();
            Header header = new Header(name, cost);
        }
        return null;
    }

    private BaseSkill load(String name) {

        for (Object object : jsonArray_) {
            JSONObject obj = (JSONObject) object;

            if (name.equals((String) obj.get("name"))) {
                EntryType type = EntryType.valueOf((String) obj.get("type"));

                if (type.equals(EntryType.SKILL)) {
                    Skill skill = loadSkill(obj);

                    if (skill != null) {
                        addSkill(name, skill);
                        return skill;
                    }
                } else if (type.equals(EntryType.HEADER)) {
                    Header header = loadHeader(obj);

                    if (header != null) {
                        addSkill(name, header);
                        return header;
                    }
                } else {
                    System.out.println("Failed to load (" + name + "), type set to (" + type + ")");
                }
            }

        }

        return null;

    }

    private Header getHeader(String name) {
        if (skillMap_.containsKey(name)) {
            BaseSkill skill = skillMap_.get(name);
            if (skill.getType().equals(EntryType.HEADER)) {
                return (Header) skill;
            }
        }
        return null;
    }

    private void addSkill(String name, BaseSkill baseSkill) {

        /*
        if (skillMap_.containsKey(name)) {
            return;
        }
                */
        if(skillMap_.containsValue(baseSkill)) {
            return;
        }

        skillMap_.put(name, baseSkill);

        if (baseSkill.getType().equals(EntryType.HEADER)) {

            headerMap_.put((Header) baseSkill, new ArrayList());
        } else if (baseSkill.getType().equals(EntryType.SKILL)) {

            Skill skill = (Skill) baseSkill;

            Header header = skill.getRequiredHeader();

            if (header != null) {
                List<Skill> headerSkills = headerMap_.get(header);
                headerSkills.add(skill);
                headerMap_.put(header, headerSkills);
            }

        }
    }
}
