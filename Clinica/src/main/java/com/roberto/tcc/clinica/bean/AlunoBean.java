package com.roberto.tcc.clinica.bean;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;

import com.roberto.tcc.clinica.dao.AlunoDAO;
import com.roberto.tcc.clinica.dao.EstadoDAO;
import com.roberto.tcc.clinica.dao.FuncaoDAO;
import com.roberto.tcc.clinica.dao.PessoaDAO;
import com.roberto.tcc.clinica.dao.SupervisorDAO;
import com.roberto.tcc.clinica.domain.Aluno;
import com.roberto.tcc.clinica.domain.Funcao;
import com.roberto.tcc.clinica.domain.Endereco;
import com.roberto.tcc.clinica.domain.Estado;
import com.roberto.tcc.clinica.domain.Pessoa;
import com.roberto.tcc.clinica.domain.Supervisor;
import com.roberto.tcc.clinica.util.CEPUtil;

@ManagedBean(name = "MBAluno")
@ViewScoped
public class AlunoBean implements Serializable {

	private static final long serialVersionUID = 841928108608962217L;

	private static final Logger logger = LogManager.getLogger(AlunoBean.class);

	private Aluno aluno = null;
	private Pessoa pessoa = null;
	private Endereco endereco = null;
	private Estado estado = null;

	private List<Estado> estados = null;
	private List<Aluno> alunos = null;
	private List<Funcao> funcoes = null;
	private List<Supervisor> supervisores = null;

	public void iniciarDomain() {
		aluno = new Aluno();
		pessoa = new Pessoa();
		endereco = new Endereco();
		estado = new Estado();
	}

	public void inicial() {
		iniciarDomain();
	}

	public void telaNovoAluno() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("novo_aluno.xhtml");
		} catch (IOException erro) {
			logger.error("Erro ao direcionar tela[novo_aluno]: " + erro);
		}
	}

	public void telaInicialAluno() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("alunos.xhtml");
		} catch (IOException erro) {
			logger.error("Erro ao direcionar tela[alunos]: " + erro);
		}
	}

	public void salvar() {
		try {
	
			this.endereco.setEstado(this.estado);
			this.pessoa.setEndereco(this.endereco);
			this.aluno.setPessoa(this.pessoa);
			this.aluno.setDataCadastro(new Date());
			AlunoDAO aDAO = new AlunoDAO();
			aDAO.salvarPessoa(this.aluno);
			
			RequestContext.getCurrentInstance().execute("PF('dlgConfirma').show();");
		} catch (RuntimeException erro) {
			logger.error("Ocorreu um erro ao tentar: " + erro);
			Messages.addGlobalError("Ocorreu um erro ao tentar salvar o aluno");
		}
	}

	public void excluir(ActionEvent evento) {
		try {
			Aluno aluno = (Aluno) evento.getComponent().getAttributes().get("alunoSelecionado");
			AlunoDAO aDAO = new AlunoDAO();
			aDAO.excluir(aluno);
			alunos = aDAO.listar();
			Messages.addGlobalInfo("Aluno excluido com sucesso");
		} catch (RuntimeException erro) {
			logger.error("Ocorreu um erro ao tentar excluir aluno: "+erro);
			Messages.addGlobalError("Ocorreu um erro ao tentar excluir o Aluno");
		}
	}

	public void verificaCPF() {

		try {
			String cpf = pessoa.getCPF();
			cpf = cpf.replace(".", "");
			cpf = cpf.replace("-", "");
			cpf = cpf.replace("_", "");
			if (cpf == null || cpf.equals("")) {
				Messages.addGlobalWarn("Informe um CPF!");
				return;
			}
			
			Pessoa pessoa = new PessoaDAO().buscarCPF(this.pessoa.getCPF());
			if (pessoa != null) {
				Aluno aluno = new AlunoDAO().buscarCodigoPes(pessoa.getCodigo());
				if(aluno != null && this.aluno.getCodigo() == null) {
					Messages.addGlobalWarn("Essa Pessoa já é um Aluno Cadastrado no Sistema!");
					this.pessoa = new Pessoa();
					return;
				}
				this.pessoa = pessoa;
				this.endereco = pessoa.getEndereco();
				this.estado = this.endereco.getEstado();
			}else {
				this.pessoa = new Pessoa();
				this.pessoa.setCPF(cpf);
			}

		} catch (RuntimeException erro) {
			logger.error("Erro ao verificar CPF: " + erro);
			Messages.addGlobalError("Ocorreu um erro interno ao verificar o CPF informado!");
		}

	}
	
	public void editar(ActionEvent evento) {
		try {
			this.aluno =  (Aluno) evento.getComponent().getAttributes().get("alunoSelecionado");
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put("codigo",
					this.aluno.getCodigo());

			telaNovoAluno();

		} catch (Exception erro) {
			logger.error("Erro ao direcionar de edição: " + erro);
		}
	}
	
	public void iniciaEdicao() {
		Long codigo = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("codigo");
		if (codigo != null) {
			this.aluno = new AlunoDAO().buscar(codigo);
			this.pessoa = aluno.getPessoa();
			this.endereco = pessoa.getEndereco();
			this.estado = endereco.getEstado();
		}

	}

	public void carregarCEP() {

		String cep = endereco.getCEP();
		cep = cep.replace(".", "");
		cep = cep.replace("-", "");
		cep = cep.replace("_", "");

		if (cep == null || cep.equals("")) {
			Messages.addGlobalWarn("Digite um CEP válido!");
			return;
		}

		try {
			JSONObject json = new CEPUtil().capturaJson(cep);
			boolean erro = json.isNull("erro");

			if (!erro) {
				Messages.addGlobalWarn("CEP informado não existe!");
				return;
			}

			endereco.setCEP(json.getString("cep"));
			endereco.setBairro(json.getString("bairro"));
			endereco.setRua(json.getString("logradouro"));

			endereco.setCidade(json.getString("localidade"));
			
			estado = new EstadoDAO().buscarSigla(json.getString("uf"));

		} catch (MalformedURLException erro) {
			logger.error("Erro ao buscar o CEP: " + erro);
			Messages.addGlobalError("Ocorreu um erro interno ao buscar o endereço");
		} catch (IOException erro) {
			logger.error("Erro ao buscar o CEP: " + erro);
			Messages.addGlobalError("Ocorreu um erro interno ao buscar o endereço");
		}
	}

	public void listarAlunos() {
		try {
			alunos = new AlunoDAO().listar();
		} catch (RuntimeException erro) {
			logger.error("Ocorreu um erro aomlistar os alunos: " + erro);
		}
	}

	public void listarFuncoes() {
		try {
			funcoes = new FuncaoDAO().listar();
		} catch (RuntimeException erro) {
			logger.error("Ocorreu um erro ao listar os Cargos: " + erro);
		}
	}

	public void listarSupervisores() {
		try {
			supervisores = new SupervisorDAO().listar();
		} catch (RuntimeException erro) {
			logger.error("Ocorreu um erro ao listar os Supervisores: " + erro);
		}
	}

	public void carregarEstados() {
		try {
			estados = new EstadoDAO().listar();
		} catch (RuntimeException erro) {
			logger.error("Erro ao listar os estados: " + erro);
			Messages.addGlobalError("Ocorreu um erro ao listar os Estados");
		}
	}

	public void novo() {
		telaNovoAluno();
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<Estado> getEstados() {
		return estados;
	}

	public void setEstados(List<Estado> estados) {
		this.estados = estados;
	}

	public List<Aluno> getAlunos() {
		return alunos;
	}

	public void setAlunos(List<Aluno> alunos) {
		this.alunos = alunos;
	}

	public List<Supervisor> getSupervisores() {
		return supervisores;
	}

	public void setSupervisores(List<Supervisor> supervisores) {
		this.supervisores = supervisores;
	}

	public List<Funcao> getFuncoes() {
		return funcoes;
	}

	public void setFuncoes(List<Funcao> funcoes) {
		this.funcoes = funcoes;
	}

}
