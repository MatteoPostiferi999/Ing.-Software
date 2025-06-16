package core.business.service;

import core.model.Guide;
import core.model.Skill;

import java.util.List;

public class GuideService {

   public void updateSkills(Guide guide, List<Skill> newSkills) {
    guide.setSkills(newSkills);
}

public void updateCredentials(Guide guide, String username, String email, String password) {
    guide.setUserName(username);
    guide.setEmail(email);
    guide.setPassword(password);
}

}
