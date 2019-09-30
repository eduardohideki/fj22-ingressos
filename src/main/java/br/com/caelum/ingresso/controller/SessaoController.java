package br.com.caelum.ingresso.controller;

import java.util.List;
import javax.validation.Valid;
import javax.xml.ws.BindingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import br.com.caelum.ingresso.dao.FilmeDao;
import br.com.caelum.ingresso.dao.SalaDao;
import br.com.caelum.ingresso.dao.SessaoDao;
import br.com.caelum.ingresso.model.Filme;
import br.com.caelum.ingresso.model.Sala;
import br.com.caelum.ingresso.model.Sessao;
import br.com.caelum.ingresso.validacao.GerenciadorDeSessao;

@Controller
public class SessaoController {
	
	@Autowired
	private FilmeDao filmeDao;
	
	@Autowired
	private SalaDao salaDao;
	
	@Autowired
	private SessaoDao sessaoDao;

	@GetMapping("/admin/sessao")
	public ModelAndView form(@RequestParam("salaId") Integer salaId, SessaoForm form) {
		
		ModelAndView modelAndView = new ModelAndView("sessao/sessao");
		
		List<Filme> filmes = filmeDao.findAll();
		
		Sala sala = salaDao.findOne(salaId);
		
		modelAndView.addObject("filmes", filmes);
		modelAndView.addObject("sala", sala);
		modelAndView.addObject("form", form);
		
		return modelAndView;
		
	}
	
	@PostMapping(value = "/admin/sessao")
	@Transactional
	public ModelAndView salva(@Valid SessaoForm form, BindingResult result) {
		
		if(result.hasErrors()) return form(form.getSalaId(), form);
		
		Sessao sessao = form.toSessao(salaDao, filmeDao);
		
		List<Sessao> sessoesDaSala = sessaoDao.buscaSessoesDaSala(sessao.getSala());
		
		GerenciadorDeSessao gerenciador = new GerenciadorDeSessao(sessoesDaSala);
		
		String pagina = "redirect:/admin/sala/" + form.getSalaId() + "/sessoes";
		
		if(gerenciador.cabe(sessao)) {
			sessaoDao.save(sessao);
			return new ModelAndView(pagina);
		}		
		
		//sessaoDao.save(sessao);
		
		//return new ModelAndView(pagina);
		
		return form(form.getSalaId(), form);
	}
}
