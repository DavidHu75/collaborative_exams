package collaborative_exams;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/Pform")
public class CreationEtAffichageQuestionForm extends HttpServlet 
{
    public static List <Question> listeQ = new ArrayList <>();
    @Inject
    GestionReponse reponse;
    @Inject
    GestionQuestion testQuestion;
    @Inject
    GestionSujet sujetT;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
        int nombreRep = Integer.parseInt(req.getParameter("nb"));
        int conditionRep =1;  
        testQuestion.initiateQuestion(Integer.parseInt(req.getParameter("identifiantQuest")));
        //testQuestion.ouvertureQuestion();
        //creation et insertion des reponses
        while(conditionRep <= nombreRep)
        {
        	String reponseId = "textReponse"+conditionRep;
        	String positionID = "pos"+conditionRep;
        	String textReponse = req.getParameter(reponseId);
        	String pos = req.getParameter(positionID);
        	//reponse.createReponse(textReponse, pos);
        	reponse.createReponse(testQuestion.getQuestion(), textReponse, pos);
        	try {
        		testQuestion.addReponse(reponse.getReponse());
        		
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
        	conditionRep=conditionRep+1;
        }//fin creation et insertion des reponses
        testQuestion.commitQuestionReponse();
        
        req.setAttribute("listQuestionR", testQuestion.retourneToutesQuestions());
        req.setAttribute("listeSujet", sujetT.getNomSujets());
        this.getServletContext().getRequestDispatcher("/afficheQuestionJPA.jsp").forward(req, resp);
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		
		if(req.getParameter("competenceR")!=null && req.getParameter("competenceR").isEmpty()!=true)
		{
			String competenceRechercher = req.getParameter("competenceR");
			req.setAttribute("listQuestionR", testQuestion.retourneListQuestionComp(competenceRechercher));
			req.setAttribute("competenceR", competenceRechercher);
	        
		}
		else if (req.getParameter("sujetR")!=null && req.getParameter("sujetR").isEmpty()!=true)
		{
			String nom = req.getParameter("sujetR");
			req.setAttribute("listQuestionR", sujetT.getQuestionsSujet(nom));
			req.setAttribute("sujetNomR",nom);
		}
		else
		{
	        req.setAttribute("listQuestionR", testQuestion.retourneToutesQuestions());
		}
		req.setAttribute("listeSujet", sujetT.getNomSujets());
		this.getServletContext().getRequestDispatcher("/afficheQuestionJPA.jsp").forward(req, resp);
		
	}
}