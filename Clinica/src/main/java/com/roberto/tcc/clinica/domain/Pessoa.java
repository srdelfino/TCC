package com.roberto.tcc.clinica.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
public class Pessoa extends GenericDomain{

	@Column(length = 50, nullable = false)
	private String nome;
	
	@Column(length = 14, nullable = false)
	private String CPF;
	
	@Column(length = 12, nullable = false)
	private String RG;
	
	@Column(nullable = false)
	private Character sexo;
	
	@Column(nullable = true)
	@Temporal(TemporalType.DATE)
	private Date dataNacimento;
	
	@Column(length = 4, nullable = false)
	private Integer idade;
	
	@Column(length = 100)
	private String escolaridade;
	
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;
	
	@Column(length = 200, nullable = false)
	private String endereco;
	
	@Column(length = 100, nullable = false)
	private String bairro;
	
	@Column(length = 50, nullable = false)
	private String numero;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Cidade cidade;
	
	@ManyToMany
	@JoinColumn(nullable = false)
	private List<Contato> telefone;
	
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Cidade getCidade() {
		return cidade;
	}
	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}
	public List<Contato> getTelefone() {
		return telefone;
	}
	public void setTelefone(List<Contato> telefone) {
		this.telefone = telefone;
	}
	@Column(length = 100, nullable = false)
	private String email;
	
	@Override
	public String toString() {
		return "Pessoa Nome: " + nome + " // CPF: " + CPF;
	}
	public String getNome() {
		return nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCPF() {
		return CPF;
	}
	public void setCPF(String cPF) {
		CPF = cPF;
	}
	public String getRG() {
		return RG;
	}
	public void setRG(String rG) {
		RG = rG;
	}
	public Character getSexo() {
		return sexo;
	}
	public void setSexo(Character sexo) {
		this.sexo = sexo;
	}
	public Date getDataNacimento() {
		return dataNacimento;
	}
	public void setDataNacimento(Date dataNacimento) {
		this.dataNacimento = dataNacimento;
	}
	public Integer getIdade() {
		return idade;
	}
	public void setIdade(Integer idade) {
		this.idade = idade;
	}
	public String getEscolaridade() {
		return escolaridade;
	}
	public void setEscolaridade(String escolaridade) {
		this.escolaridade = escolaridade;
	}
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
}
