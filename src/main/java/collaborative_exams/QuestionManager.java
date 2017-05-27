package collaborative_exams;
import java.util.List;
import java.util.StringTokenizer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.derby.tools.sysinfo;

@RequestScoped
@Transactional
public class QuestionManager 
{
	Question questionT;
	private static final String PERSISTENCE_UNIT_NAME = "questT";
    private static EntityManagerFactory factory;
	@PersistenceContext(unitName = "questT")
    EntityManager em; 
	public void createQuestion(String name, String language, String skill, String statement, int id, String level)
    {
    	this.questionT = new Question();
    	this.questionT.setAut(name);
    	this.questionT.setLanguage(language);
    	this.questionT.setSkill(skill);
    	this.questionT.setStatement(statement);
    	this.questionT.setId(id);
    	this.questionT.setLevel(level);
    	this.questionT.nbVoteRelevance = 0;
    	this.questionT.totalRelevanceMark = 0;
    	questionT.setQuestionnaireNew();

    }
	public void setAnswerG (Answer a)
	{
		this.questionT.setAnswer(a);
	}
	public Question getQuestion()
	{
		return this.questionT;
	}
	public Question createQuestionV(String name, String language, String skill, String statement, String idParent, int id, String opinion, String level)
    {
    	questionT = new Question(idParent);
    	questionT.setAut(name);
    	questionT.setLanguage(language);
    	questionT.setSkill(skill);
    	questionT.setStatement(statement);
    	questionT.setVar(idParent);
    	questionT.setId(id);
        questionT.setOpinion(opinion);
        questionT.setLevel(level);
        questionT.nbVoteRelevance = 0;
        questionT.totalRelevanceMark = 0;
        
		return questionT;
    }
	public void addAnswer(Answer ans) throws Exception 
	{
		
        em.persist(ans);
        em.flush();
        this.setAnswerG(ans);
        
    }
	public void openQuestion(String name, String language, String skill, String statement, int id, String level)
	{
		this.createQuestion(name, language, skill, statement, id, level);
		em.persist(this.questionT);
		questionT.setIdV(questionT.idtechnique);
		em.flush();
		questionT.setIdV(this.questionT.idtechnique);
	}
	
	public void initiateQuestion(int id)
	{
		Query q = em.createQuery("SELECT u FROM Question u where u.idTechvisible =:arg1", Question.class);
        q.setParameter("arg1", id);
        this.questionT = new Question();
        this.questionT = (Question) q.getResultList().get(0);
        //em.persist(this.questionT);

	}
	//with session
	public void commitQuestion()
	{   
		em.persist(questionT);
	}
	
	public void commitQuestionAnswer()
	{  	em.merge(questionT);
		em.flush();
	}
	public List <Question> returnAllQuestions()
	{   
	    Query q = em.createQuery("SELECT u FROM Question u", Question.class);
	    List <Question> searchList = q.getResultList();
	    return searchList;
	}
	public List <Question> returnDiff(List <Question> questTemp)
	{   
		factory = Persistence.createEntityManagerFactory("questT");
    	em = factory.createEntityManager();
    
	    Query q = em.createQuery("SELECT u FROM Question u", Question.class);
	    List <Question> searchList = q.getResultList();
	    
	    for(Question s : questTemp)
	    {
	    	for (int i = 0; i<searchList.size(); i++)
	    	{
	    		if(searchList.get(i).getIdTech()==s.getIdTech())
		    	{
	    			searchList.remove(i);
		    	}
	    	}
	    }
	    	
	    em.close();
	    return searchList;
	}
	public Question returnQuestion(String id)
	{
        // Read the existing entries and write to console
        Query q = em.createQuery("SELECT u FROM Question u where u.idTechvisible =:arg1", Question.class);
        q.setParameter("arg1", Integer.parseInt(id));
        Question searchList = (Question) q.getResultList().get(0);
        return searchList;
	}
	
	public Question returnQuestionMark(String id, int note)
	{
        
        // Read the existing entries and write to console
        Query q = em.createQuery("SELECT u FROM Question u where u.idTechvisible =:arg1", Question.class);
        q.setParameter("arg1", Integer.parseInt(id));
        Question searchList = (Question) q.getResultList().get(0);
        searchList.setRelevanceMark(note);
        em.merge(searchList);
        return searchList;
	}
	public List <Question> returnListQuestionSkill(String comp)
	{
        Query q = em.createQuery("SELECT u FROM Question u where u.competenceQ like :arg1", Question.class);
        q.setParameter("arg1", comp);
        List <Question> searchList = q.getResultList();
        return searchList;
	}
	
    public void modifyQuestion(String name, String language, String skill, String statement, int id, String level)
    {
        factory = Persistence.createEntityManagerFactory( "questT" );
        em = factory.createEntityManager( );
        em.getTransaction( ).begin( );
        Question quest = em.find(Question.class, id);
       
        this.questionT.setAut(name);
        this.questionT.setLanguage(language);
        this.questionT.setSkill(skill);
        this.questionT.setStatement(statement);
        this.questionT.setLevel(level);
        em.getTransaction( ).commit( );
        
        System.out.println(quest);
        em.close();
        factory.close();
    }
    
    public void deleteQuestion( int id)
    {
        factory = Persistence.createEntityManagerFactory( "questT" );
        em = factory.createEntityManager( );
        em.getTransaction( ).begin( );
        Question quest = em.find(Question.class, id);
        
        System.out.println(quest);
        em.remove(quest);
        em.getTransaction( ).commit( );
        em.close();
        factory.close();
    }
    public List<Question> getQuestionsSubject(String nameSubject)
    {
        // Read the existing entries and write to console
        Query q = em.createQuery("SELECT u FROM Subject u where u.nameSubject like :arg1", Subject.class);
        q.setParameter("arg1", nameSubject);
        List<Subject> subjectTemp = q.getResultList();
		return subjectTemp.get(0).getQuestionsSubject();
    	
    }
    
}