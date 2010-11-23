Cada execução do programa será usada para representar um Nó da topologia.

Primeiramente deve-se colocar os arquivos de configuração citados na descrição do projeto mais o arquivo distanciaMaxima.config na mesma pasta
que se encontra o arquivo ProjetoRedes-Grupo10.jar. Os arquivos devem ter as seguintes características:
- nome: roteador.config 
	O formato de cada linha desse arquivo deve ser o seguinte: identificador do roteador (inteiro), número da porta e número IP  (espaçamento livre entre valores).

- nome: enlaces.config 
	O formato de cada linha do arquivo deve ser:  ID roteador   ID roteador   custo      (espaçamento livre entre valores).

- nome: distanciaMaxima.config
	Esse arquivo deve conter apenas uma linha com um número informando o diâmetro da rede. Esse número deve ser maior que 0.


Deve-se executar o programa e fornecer o ID do Nó o qual deseja-se simular com aquele processo.

Para criar uma nova execução do programa basta na linha de comando ir para a pasta onde está localizado
o arquivo ProjetoRedes-Grupo10.jar e digitar: java -jar projeto-redes-20102_Grupo-10.jar