# CBMM Ship Simulator

Aplicação Android para simulação de operações de navios e contêineres da CBMM.

## Configuração do Ambiente

1. **Baixe e instale o Android Studio**
   - Acesse: [developer.android.com/studio](https://developer.android.com/studio)
   - Siga as instruções de instalação para o seu sistema operacional

2. **Obtenha uma chave da API do Google Maps**
   - Acesse: [console.cloud.google.com](https://console.cloud.google.com/)
   - Crie um novo projeto ou selecione um existente
   - Ative a API do Maps SDK para Android
   - Crie uma chave de API
   - No arquivo `app/src/main/AndroidManifest.xml`, substitua `YOUR_API_KEY` pela sua chave de API:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="SUA_CHAVE_DE_API_AQUI" />
     ```

3. **Importe o projeto no Android Studio**
   - Abra o Android Studio
   - Selecione "Open an Existing Project"
   - Navegue até a pasta `CBMM-ShipSimulator` e selecione
   - Aguarde a sincronização do Gradle terminar

4. **Execute o aplicativo**
   - Conecte um dispositivo Android via USB com a depuração USB ativada
   - Ou crie um emulador: Tools > Device Manager > Create Device
   - Clique no botão "Run" (ícone de play verde) na barra de ferramentas

## Estrutura do Projeto

- `app/src/main/java/com/cbmm/shipsimulator/`
  - `data/` - Modelos e repositórios de dados
  - `ui/` - Componentes de interface do usuário
  - `util/` - Utilitários e extensões
  - `CBMMShipSimulatorApp.kt` - Classe principal do aplicativo
  - `MainActivity.kt` - Atividade principal

- `app/src/main/res/`
  - `drawable/` - Recursos de imagem
  - `layout/` - Layouts XML
  - `navigation/` - Gráfico de navegação
  - `values/` - Cores, estilos e strings

## Dependências Principais

- Jetpack Compose para UI
- ViewModel e LiveData para gerenciamento de estado
- Navigation Component para navegação
- Google Maps para exibição do mapa
- Coroutines para operações assíncronas

## Recursos Implementados

- Mapa interativo com marcadores de navios e portos
- Diálogos detalhados para navios e portos
- Operações de navio: partida, chegada, carregamento e descarregamento
- Interface moderna com tema da CBMM

## Próximos Passos

- Implementar mais telas (Frota, Portos, Análises, Configurações)
- Adicionar mais dados de simulação
- Melhorar a interface do usuário
- Adicionar testes automatizados
