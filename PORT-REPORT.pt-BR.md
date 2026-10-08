# Multi Builder Tool — ATM11 0.9.0

Alvo: ATM11 0.9.0, Minecraft 26.1.2, NeoForge 26.1.2.109, Java 25.

Copie somente o JAR desta pasta para a pasta mods da instância. Substitua versões anteriores do mesmo mod; os JARs de dependências já presentes no ATM11 continuam necessários. Cada mod possui sua própria pasta de entrega.

Os testes foram feitos em mundos de desenvolvimento separados da instância do CurseForge. A instância e os saves do usuário não foram modificados. Os testes confirmam os cenários descritos abaixo; ainda é necessário validar no pack completo, especialmente automação prolongada e integrações com outros mods.

O JAR contém as classes e os recursos do mod, sem incluir cópias das dependências ou as classes de testes. Todos os arquivos Java da produção possuem sua classe no JAR; nenhuma família de recursos foi removida para obter a compilação.

Base: código oficial MIT, branch 26.1 de https://github.com/igentuman/multi-builder-tool
O arquivo oficial 1.3.0 já tinha base 26.1.2; esta entrega completa pendências e corrige problemas verificados no código dessa versão.
JEI 29.37.0.99, Mekanism Version Locked 2.1, AE2 26.1.12-beta e GuideME 26.1.12-beta usados na compilação/testes.

Completado o registro das capacidades modernas de energia e inventário do item. Corrigidos salvamento e retirada de pilhas de até 512 itens, rollback de transações, textos com alpha e crash de blur duplicado na galeria. Mantidos escolha de estrutura, previews, construção, desmontagem, rotações, equivalência de blocos e integração AE2/autocrafting.

Testes reais: carregamento de energia e inventário; rollback; pilha de 512 itens após serialização; construção em survival de matriz Mekanism de 36 blocos com consumo exato de materiais/energia; desmontagem da estrutura. Cliente: inventário da ferramenta, galeria e seletor com estruturas sincronizadas e meshes 3D reais renderizados. Autocrafting de um pedido completo através de uma rede AE2 não foi exercitado no cenário automatizado.

Auditoria: 82 classes, 12 arquivos JSON; bytecode Java 25.

JAR: `MultiBuilder-26.1-1.3.0-atm11-0.9.0.jar`

SHA-256: `38badb4c89539f0e6b1904ec87eb2e828569baaff120765f536e0aceb4f79767`
