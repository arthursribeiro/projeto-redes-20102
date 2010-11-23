Cada execu��o do programa ser� usada para representar um N� da topologia.

Primeiramente deve-se colocar os arquivos de configura��o citados na descri��o do projeto mais o arquivo distanciaMaxima.config na mesma pasta
que se encontra o arquivo ProjetoRedes-Grupo10.jar. Os arquivos devem ter as seguintes caracter�sticas:
- nome: roteador.config 
	O formato de cada linha desse arquivo deve ser o seguinte: identificador do roteador (inteiro), n�mero da porta e n�mero IP  (espa�amento livre entre valores).

- nome: enlaces.config 
	O formato de cada linha do arquivo deve ser:  ID roteador   ID roteador   custo      (espa�amento livre entre valores).

- nome: distanciaMaxima.config
	Esse arquivo deve conter apenas uma linha com um n�mero informando o di�metro da rede. Esse n�mero deve ser maior que 0.


Deve-se executar o programa e fornecer o ID do N� o qual deseja-se simular com aquele processo.

Para criar uma nova execu��o do programa basta na linha de comando ir para a pasta onde est� localizado
o arquivo ProjetoRedes-Grupo10.jar e digitar: java -jar projeto-redes-20102_Grupo-10.jar