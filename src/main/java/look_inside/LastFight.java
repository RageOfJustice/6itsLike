package look_inside;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;


import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LastFight extends JFrame{

    private final String SCOPE = "audio";
    private final String CLIENT_ID = "5491279";
    private final String bandExample = "исполнитель название_песни";
    private final String EUROPA_URL = "http://ep128.hostingradio.ru:8030/ep128";
    private final String TOP_40_URL = "http://eptop128server.streamr.ru:8033/eptop128";
    private final String LIGHT_URL = "http://emg02.hostingradio.ru/ep-light128.mp3";
    private final String NEW_URL = "http://emg02.hostingradio.ru/ep-new128.mp3";
    private final String RGB_URL = "http://eprnb128server.streamr.ru:8061/eprnb128";
    private final String RESIDANCE_URL = "http://emg02.hostingradio.ru/ep-residance128.mp3";

    private String access_token;
    private String login;
    private String password;
    private String station=EUROPA_URL;

    private Player player;

    private Thread playerThread;

    private JTextField music = new JTextField(100);

    private JMenuBar mainBar = new JMenuBar();

    private JPanel contentPanel= new JPanel();

    private JTextField musicTabla = new JTextField();

    private JMenuItem playRadio = new JMenuItem("Играть радио");
    private JMenuItem authorize = new JMenuItem("Авторизация");
    private JMenuItem musicName = new JMenuItem("Название песни");
    private JMenu addToVk = new JMenu("Добавить в ВК");
    private JMenu selectStation = new JMenu("Дополнительные станции");
    private JMenuItem addToVkByRadio = new JMenuItem("Из радио");
    private JMenuItem addToVkByInsert = new JMenuItem("Написать название");
    private JRadioButtonMenuItem europaPlus = new JRadioButtonMenuItem("Европа плюс");
    private JRadioButtonMenuItem top40 = new JRadioButtonMenuItem("TOP 40");
    private JRadioButtonMenuItem light = new JRadioButtonMenuItem("Light");
    private JRadioButtonMenuItem _new = new JRadioButtonMenuItem("New");
    private JRadioButtonMenuItem rgb = new JRadioButtonMenuItem("R&B");
    private JRadioButtonMenuItem residance = new JRadioButtonMenuItem("Residance");

    private boolean authorized;

    private boolean isAuthorized(){return this.authorized;}

    private void setAuthorized(boolean value){this.authorized = value;}

    private JButton getJButton(String title){
        JButton res = new JButton(title);
        res.setMaximumSize(new Dimension(150,30));
        return res;
    }

    private String getAccessToken(String login, String password) {
        CookieHandler.setDefault(new CookieManager());
        String access_token = "";

        int count = 0;
        int maxTries = 2;
        do {
            try {
                URL url = new URL("https://oauth.vk.com/authorize?" +
                        "client_id=" + CLIENT_ID +
                        "&scope=" + SCOPE +
                        "&redirect_uri=http://oauth.vk.com/blank.html" +
                        "&display=page&response_type=token");

                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");

                BufferedReader rd = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "UTF8"));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                rd.close();

                httpConnection.disconnect();

                String response = stringBuilder.toString();

                String _origin;
                String ip_h;
                String lg_h;
                String to;

                //Parsing the action attribute
                String parsingRes = RegexPatterns.parseAction(response);
                if (parsingRes.equals("")) {
                    throw new Exception();
                }
                url = new URL(parsingRes);

                //Parsing the _origin input
                parsingRes = RegexPatterns.parseInput(RegexPatterns.PAT__ORIGIN, response);
                if (parsingRes.equals("")) {
                    throw new Exception();
                }
                _origin = parsingRes;

                //Parsing the ip_h input
                parsingRes = RegexPatterns.parseInput(RegexPatterns.PAT_IP_H, response);
                if (parsingRes.equals("")) {
                    throw new Exception();
                }
                ip_h = parsingRes;

                //Parsing the lg_h input
                parsingRes = RegexPatterns.parseInput(RegexPatterns.PAT_LG_H, response);
                if (parsingRes.equals("")) {
                    throw new Exception();
                }
                lg_h = parsingRes;

                //Parsing the to input
                parsingRes = RegexPatterns.parseInput(RegexPatterns.PAT_TO, response);
                if (parsingRes.equals("")) {
                    throw new Exception();
                }
                to = parsingRes;

                String requestContents = "_origin=" + _origin +
                        "&ip_h=" + ip_h +
                        "&lg_h=" + lg_h +
                        "&to=" + to +
                        "&email=" + login +
                        "&pass=" + password;

                httpConnection = (HttpURLConnection) url.openConnection();

                httpConnection.setRequestMethod("POST");
                httpConnection.setDoOutput(true);
                httpConnection.setDoInput(true);
                httpConnection.setRequestProperty("Content-Length", "" + requestContents.length());
                httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpConnection.setRequestProperty("content", "text/html; charset=UTF-8");

                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(httpConnection.getOutputStream()));
                wr.write(requestContents);
                wr.flush();
                wr.close();

                rd = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                stringBuilder = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                rd.close();
                response = stringBuilder.toString();

                String targetUrl = httpConnection.getURL().toString();

                httpConnection.disconnect();

                Pattern pattern2 = Pattern.compile("<b>");
                Matcher matcher2 = pattern2.matcher(response);
                if (!matcher2.find()) {


                    //Parsing the action attribute
                    parsingRes = RegexPatterns.parseAction(response);
                    if (parsingRes.equals("")) {
                        throw new Exception();
                    }
                    url = new URL(parsingRes.substring(0, parsingRes.indexOf("?") + 1));
                    requestContents = parsingRes.substring(parsingRes.indexOf("?") + 1);

                    HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();

                    httpsConnection.setRequestMethod("POST");
                    httpsConnection.setDoOutput(true);
                    httpsConnection.setDoInput(true);
                    httpsConnection.setRequestProperty("Content-Length", "" + requestContents.length());
                    httpsConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpsConnection.setRequestProperty("content", "text/html; charset=UTF-8");

                    httpsConnection.connect();

                    wr = new BufferedWriter(new OutputStreamWriter(httpsConnection.getOutputStream()));
                    wr.write(requestContents);
                    wr.flush();
                    wr.close();

                    rd = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    rd.close();
                    targetUrl = httpsConnection.getURL().toString();

                    httpsConnection.disconnect();
                } else

                    //Parsing the url for the access_token
                    parsingRes = RegexPatterns.parseAccessToken(targetUrl);
                if (parsingRes.equals("")) {
                    throw new Exception();
                }
                access_token = parsingRes;

                count = maxTries;

            } catch (ConnectException exc) {
                count++;
            } catch (Exception exc) {
                count = maxTries;
            }
        } while (count != maxTries);
        return access_token;
    }

    /**
     * Создает новую панель с менеджером BoxLayout по нужному направлению,
     * отступами и самими компонентами
     * @param strut величина отступа
     * @param axisX true - если по оси X, false - по Y
     * @param components компоненты для добваления
     * @return заполненная панель
     */
    private JPanel getPanel(int strut,boolean axisX,JComponent... components){
        JPanel panel = new JPanel();
        //panel.setSize(getWidth(),150);
        if(axisX){
            panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
            panel.setMaximumSize(new Dimension( getWidth(),200));
        }
        else{
            panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
            panel.setMaximumSize(new Dimension(200, getHeight()));
        }
        for(JComponent component: components){
            panel.add(component);
            if(axisX)
                panel.add(Box.createHorizontalStrut(strut));
            else panel.add(Box.createVerticalStrut(strut));
        }
        return panel;
    }

    private void initRadio(){
        try {
            URL url = new URL(station);
            InputStream fin = url.openStream();
            InputStream is = new BufferedInputStream(fin);

            player = new Player(is);
        }
        catch (FileNotFoundException e)
        {
            getMessageFrame("Ошибка","Url "+station+" не найден");
        }
        catch (Exception e)
        {
            getMessageFrame("Ошибка","При проигрывании с потока "+station+ " возникла следующая ошибка:");
        }
    }


    private JFrame getAuthorizeFrame(String title){
        final JFrame frame = new JFrame(title);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 300, height = 300;
        int locationX = (screenSize.width - width) / 2;
        int locationY = (screenSize.height - height) / 2;
        frame.setBounds(locationX, locationY, width, height);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(width,height);
        frame.setVisible(true);

        final JTextField loginField = new JTextField(login);
        loginField.setMaximumSize(new Dimension(100,20));
        loginField.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JPasswordField passwordField = new JPasswordField(password);
        passwordField.setMaximumSize(new Dimension(100,20));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));

        JPanel loginPane = new JPanel();
        loginPane.setLayout(new BoxLayout(loginPane,BoxLayout.X_AXIS));
        loginPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPane.add(new JLabel("Логин"));
        loginPane.add(Box.createHorizontalStrut(15-"Логин".length()));
        loginPane.add(loginField);
        contentPane.add(loginPane);
        contentPane.add(Box.createVerticalStrut(10));

        JPanel passwordPane =new JPanel();
        passwordPane.setLayout(new BoxLayout(passwordPane,BoxLayout.X_AXIS));
        passwordPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordPane.add(new JLabel("Пароль"));
        passwordPane.add(Box.createHorizontalStrut(15-"Пароль".length()));
        passwordPane.add(passwordField);
        contentPane.add(passwordPane);
        contentPane.add(Box.createVerticalStrut(10));

        JButton accept = getJButton("Войти");
        accept.setAlignmentX(Component.CENTER_ALIGNMENT);
        accept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login = loginField.getText();
                password = "";
                for (char el : passwordField.getPassword()) {
                    password += el;
                }
                if (login.isEmpty() || password.isEmpty()) {
                    getMessageFrame("Ошибка", "Не введен логин/пароль");
                    return;
                }
                access_token = getAccessToken(login, password);
                if (!access_token.isEmpty()) {
                    setAuthorized(true);
                    //authorize.setVisible(false);
                    frame.dispose();
                } else {
                    getMessageFrame("Ошибка", "Не удалось авторизироваться");
                }
            }
        });
        contentPane.add(accept);

        frame.add(contentPane,BorderLayout.CENTER);

        return frame;
    }


    /**
     * Получает название песни с радио европы плюс
     * @return исполнитель песня
     * @throws IOException
     */
    private String getMusicName() throws IOException {
        String musicName;

        String req = "http://m.europaplus.ru/player";

        Document site = Jsoup.connect(req).get();

        Elements songs= site.body().getElementsByClass("song");

        Element curSong = songs.get(0);

        String author =  curSong.child(0).text();
        String song =  curSong.child(1).text();

        musicName = author +" " +song;

        return musicName;
    }

    /**
     * Добавляет музыку в контакт используя API
     * @param music исполнитель песня
     * @return true - если добавлена
     * @throws IOException
     * @throws JSONException
     */
    private boolean addMusic(String music) throws IOException, JSONException {
        String req = "https://api.vk.com/method/audio.search?q="+music+
                "&auto_complete=1" +
                "&performer_only=1"+
                "&sort=2" +
                "&count=1" +
                "&access_token="+access_token;
        URL url = new URL(req);

        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "UTF8"));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        rd.close();

        httpConnection.disconnect();

        if(stringBuilder.toString().isEmpty()){
            getMessageFrame("Ошибка","Ошибка запроса");
            return false;
        }

        JSONArray search_res = new JSONObject(stringBuilder.toString()).getJSONArray("response");

        if(search_res.getInt(0)==0){
            req = "https://api.vk.com/method/audio.search?q="+music+
                    "&auto_complete=1" +
                    "&sort=2" +
                    "&count=1" +
                    "&access_token="+access_token;
            url = new URL(req);

            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            rd = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "UTF8"));
            stringBuilder = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            rd.close();

            httpConnection.disconnect();

            if(new JSONObject(stringBuilder.toString()).getJSONArray("response").getInt(0)==0) {
                getMessageFrame("Ошибка", "Аудиозапись не найдена");
                return false;
            }
        }

        int audioId = search_res.getJSONObject(1).getInt("aid"), ownerId = search_res.getJSONObject(1).getInt("owner_id");

        req = "https://api.vk.com/method/audio.add?audio_id="+audioId+
                "&owner_id="+ ownerId +
                "&access_token="+access_token;

        url = new URL(req);
        httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestMethod("GET");

        stringBuilder = new StringBuilder();
        rd = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "UTF8"));

        while ((line = rd.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        rd.close();

        httpConnection.disconnect();

        if(stringBuilder.toString().isEmpty()){
            getMessageFrame("Ошибка","Ошибка добавления аудиозаписи");
            return false;
        } else
            getMessageFrame("Готово","Аудиозапись добавлена");
        return true;
    }

    /**
     * Создает окно с выводом сообщения
     * @param title заголовок окна
     * @param message выводимое сообщение
     * @return итоговый фрейм
     */
    private JFrame getMessageFrame(String title, String message){
        final JFrame frame = new JFrame(title);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int sizeWidth = 200;
        int sizeHeight = 150;
        int locationX = (screenSize.width - sizeWidth) / 2;
        int locationY = (screenSize.height - sizeHeight) / 2;
        frame.setBounds(locationX, locationY, sizeWidth, sizeHeight);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(sizeWidth,sizeHeight);
        frame.setVisible(true);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
        frame.setContentPane(contentPane);

        JLabel mess = new JLabel(message);
        mess.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(mess);
        contentPane.add(Box.createVerticalStrut(30));

        JButton accept = getJButton("Ок");
        accept.setAlignmentX(CENTER_ALIGNMENT);
        accept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        contentPane.add(accept);
        return frame;
    }

    private void stopPlayer(){
        if(playerThread!=null && playerThread.isAlive()){
            player.close();
            playerThread.interrupt();
            playRadio.setText("Играть радио");
        }
    }

    private void initStation(String url, boolean VK, boolean name){
        station = url;
        addToVkByRadio.setEnabled(VK);
        musicName.setEnabled(name);
    }

    public LastFight(){

        super("Last fight");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int sizeWidth = 700;
        int sizeHeight = 200;
        int locationX = (screenSize.width - sizeWidth) / 2;
        int locationY = (screenSize.height - sizeHeight) / 2;
        setBounds(locationX, locationY, sizeWidth, sizeHeight);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(sizeWidth,sizeHeight);
        setResizable(false);
        setVisible(true);
        setJMenuBar(mainBar);

        contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        add(contentPanel,BorderLayout.CENTER);

        musicTabla.setEditable(false);
        musicTabla.setMaximumSize(new Dimension(300,30));

        authorize.setMaximumSize(new Dimension(150,100));
        authorize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAuthorized()) {
                    getAuthorizeFrame("Авторизация");
                }
            }
        });

        mainBar.add(authorize);

        music.setMaximumSize(new Dimension(200,30));
        music.setText(bandExample);
        music.setForeground(Color.gray);
        music.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField textF =((JTextField)e.getSource());
                if(textF.getText().equals(bandExample))
                    ((JTextField)e.getSource()).setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                JTextField textF =((JTextField)e.getSource());
                if(textF.getText().isEmpty()) textF.setText(bandExample);
            }
        });
        final JButton addMus = getJButton("Добавить");
        addMus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (music.getText().isEmpty() || music.getText().equals(bandExample)) {
                    getMessageFrame("Ошибка", "Введите песню");
                }
                if (isAuthorized()) {
                    try {
                        if (addMusic(music.getText().replaceAll(" ", "+"))) {
                            music.setText("");
                        }
                    } catch (IOException | JSONException e1) {
                        e1.printStackTrace();
                    }
                } else getMessageFrame("Ошибка", "Нужно авторизироваться");
            }
        });

        playRadio.setMaximumSize(new Dimension(150,100));
        playRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!playRadio.getText().equals("Стоп")) {
                    initRadio();
                    playerThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                player.play();
                            } catch (JavaLayerException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                    playerThread.start();
                    playRadio.setText("Стоп");
                } else {
                    player.close();
                    playerThread.interrupt();
                    playRadio.setText("Играть радио");
                }
            }
        });

        musicName.setMaximumSize(new Dimension(150,100));
        musicName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    musicTabla.setText(getMusicName());
                    contentPanel.add(musicTabla, 0);
                    revalidate();
                    repaint();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        addToVkByRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isAuthorized()) {
                    contentPanel.removeAll();
                    revalidate();
                    repaint();
                    try {
                        addMusic(getMusicName().replaceAll(" ", "+"));
                    } catch (IOException | JSONException e1) {
                        e1.printStackTrace();
                    }
                } else getMessageFrame("Ошибка", "Нужно авторизироваться");
            }
        });

        addToVkByInsert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentPanel.removeAll();
                contentPanel.add(getPanel(50, true, new JLabel("Аудиозапись"), music, addMus));
                revalidate();
                repaint();
            }
        });

        europaPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initStation(EUROPA_URL, true, true);
                initRadio();
                stopPlayer();
            }
        });

        top40.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initStation(TOP_40_URL, false, false);
                initRadio();
                stopPlayer();
            }
        });

        light.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initStation(LIGHT_URL, false, false);
                initRadio();
                stopPlayer();
            }
        });

        _new.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initStation(NEW_URL, false, false);
                initRadio();
                stopPlayer();
            }
        });

        rgb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initStation(RGB_URL,false,false);
                initRadio();
                stopPlayer();
            }
        });

        residance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initStation(RESIDANCE_URL, false, false);
                initRadio();
                stopPlayer();
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(europaPlus);
        group.add(top40);
        group.add(light);
        group.add(_new);
        group.add(rgb);
        group.add(residance);

        selectStation.add(europaPlus);
        europaPlus.setSelected(true);
        selectStation.addSeparator();
        selectStation.add(top40);
        selectStation.add(light);
        selectStation.add(_new);
        selectStation.add(rgb);
        selectStation.add(residance);

        mainBar.add(playRadio);
        mainBar.add(musicName);
        mainBar.add(addToVk);
        mainBar.add(selectStation);

        addToVk.add(addToVkByRadio);
        addToVk.add(addToVkByInsert);
    }

    public static void main(String... args){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LastFight();
            }
        });
    }

}
