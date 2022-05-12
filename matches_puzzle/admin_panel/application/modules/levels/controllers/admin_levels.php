<?php

if (!defined("BASEPATH"))
    exit("No direct script access allowed");

class Admin_levels extends back {

    private $c_name = "admin_levels_";

    public function __construct() {
        parent::__construct();
        $this->lang->load("levels", "english");
        $this->load->model("levels_model", "levels");
        $this->load->library("form_validation");
        $this->form_validation->set_error_delimiters("<label class='error'>", "</label>");
        $this->_is_logged_in();
    }

    //=====================================================================

    public function index() {
        $this->overview();
    }

    //=====================================================================

    public function overview() {

        $this->session->set_userdata("page_number", $this->uri->segment(4));
        $per_page_sess = $this->session->userdata($this->c_name . "per_page_sess");
        $this->load->library("pagination");

        $config["base_url"] = base_url() . "levels/admin_levels/overview/";
        $config["total_rows"] = $this->levels->get_levels_count();
        $config["per_page"] = ($per_page_sess) ? $per_page_sess : 10;
        $config["uri_segment"] = 4;
        $config["use_page_numbers"] = TRUE;
        $config['num_links'] = 3;

        $config['num_tag_open'] = '<li>';
        $config['num_tag_close'] = '</li>';

        $config['cur_tag_open'] = '<li class="active"><a href="#">';
        $config['cur_tag_close'] = '</a></li>';

        $config['first_link'] = 'First';
        $config['first_tag_open'] = '<li>';
        $config['first_tag_close'] = '</li>';

        $config['last_link'] = 'Last';
        $config['last_tag_open'] = '<li>';
        $config['last_tag_close'] = '</li>';

        $config['prev_link'] = 'Prev';
        $config['prev_tag_open'] = '<li>';
        $config['prev_tag_close'] = '</li>';

        $config['next_link'] = 'Next';
        $config['next_tag_open'] = '<li>';
        $config['next_tag_close'] = '</li>';

        $this->pagination->initialize($config);

        $data["pagination"] = $this->pagination->create_links();
        $data["levels"] = $this->levels->overview($config["per_page"], $this->uri->segment(4));
        if (!$data["levels"] && $this->uri->segment(4) >= 2) {
            $data["levels"] = $this->levels->overview($config["per_page"], $this->uri->segment(4) - 1);
        }
        $data["per_page_num"] = $per_page_sess;
        $this->view("back/overview", $data);
    }

    //=====================================================================

    private function set_form_validation_rules() {
        $this->form_validation->set_rules("le_number", lang("input_number"), "required|trim|xss_clean|numeric|htmlspecialchars");
        $this->form_validation->set_rules("le_x_value", lang("input_x_value"), "required|trim|xss_clean|is_natural|less_than[10]|htmlspecialchars");
        $this->form_validation->set_rules("le_y_value", lang("input_y_value"), "required|trim|xss_clean|is_natural|less_than[10]|htmlspecialchars");
        $this->form_validation->set_rules("le_r_value", lang("input_r_value"), "required|trim|xss_clean|is_natural|less_than[10]|htmlspecialchars");
        $this->form_validation->set_rules("le_operator", lang("input_operator"), "required|trim|xss_clean|htmlspecialchars");
        $this->form_validation->set_rules("le_moves", lang("input_moves"), "required|trim|xss_clean|htmlspecialchars");
        $this->form_validation->set_rules("le_solution", lang("input_solution"), "required|trim|xss_clean|htmlspecialchars");
        $this->form_validation->set_rules("le_status", lang("input_status"), "");
    }

    //=====================================================================

    public function create() {

        $this->set_form_validation_rules();

        $data["max_le_number"] = $this->levels->get_max_level();

        $data["is_equation_exist"] = $this->levels->is_equation_exist();
        if ($this->form_validation->run() == FALSE || $data["is_equation_exist"]) {
//            if ($data["is_equation_exist"]) {
//                $data["equation_exists"] = lang("already_exists_msg") . $data["is_equation_exist"]->le_number;
//            }
            $this->view("back/create", $data);
        } else {
            $this->levels->create();
            $this->redirect_overview(lang("noti_success_added"));
        }
    }

    //=====================================================================        

    public function edit($_leid) {
        $data["level"] = $this->levels->get_this_level($_leid);
        $this->set_form_validation_rules();

        $data["is_equation_exist"] = $this->levels->is_equation_exist();
        if ($this->form_validation->run() == FALSE || ($data["is_equation_exist"] && $data["is_equation_exist"]->_leid != $data["level"]->_leid)) {
            $this->view("back/edit", $data);
        } else {

            $this->levels->edit($_leid);
            $this->redirect_overview(lang("noti_success_updated"), $this->session->userdata("page_number"));
        }
    }

    //=====================================================================       

    public function order_up($_leid) {

        if ($this->levels->order_up($_leid)) {
            $this->redirect_overview(lang("noti_success_reordered"), $this->session->userdata("page_number"));
        }
    }

    //=====================================================================

    public function order_down($_leid) {

        if ($this->levels->order_down($_leid)) {
            $this->redirect_overview(lang("noti_success_reordered"), $this->session->userdata("page_number"));
        }
    }

    //=====================================================================        

    public function operation() {
        if ($_POST["per_page"] != $this->session->userdata($this->c_name . "per_page_sess")) {
            $noti = "";
            $this->session->set_userdata($this->c_name . "per_page_sess", $_POST["per_page"]);
            $this->session->unset_userdata("page_number");
        } else {
            if (isset($_POST["activate"])) {
                $noti = lang("noti_success_activated");
            } elseif (isset($_POST["deactivate"])) {
                $noti = lang("noti_success_deactivated");
            } elseif (isset($_POST["delete"])) {
                $noti = lang("noti_success_deleted");
            }
            $this->levels->operation();
        }
        $this->redirect_overview($noti, $this->session->userdata("page_number"));
    }

    //=====================================================================

    public function activate($_leid) {
        if ($this->levels->activate($_leid)) {
            $this->redirect_overview(lang("noti_success_activated"), $this->session->userdata("page_number"));
        }
    }

    //=====================================================================

    public function deactivate($_leid) {
        if ($this->levels->deactivate($_leid)) {
            $this->redirect_overview(lang("noti_success_deactivated"), $this->session->userdata("page_number"));
        }
    }

    //=====================================================================

    public function delete($_leid, $le_number) {
        if ($this->levels->delete($_leid, $le_number)) {
            $this->redirect_overview(lang("noti_success_deleted"), $this->session->userdata("page_number"));
        }
    }

    //=====================================================================

    public function insert_emails() {

        $emails = "app.6677g@gmail.com,metro@metrotrains.com.au,info@paraone.co,vagtal@gmail.com,android@tapsarena.com,melai0707@gmail.com,logoquizfull@hotmail.com,info@nekjo.com,Xocrais@gmail.com,fesgames@gmail.com,paol.boi@gmail.com,piotr.kaczanowski@gazeta.pl,akkelabs@gmail.com,cristinamontolio.cm@gmail.com,marcellkristof.elek@gmail.com,tiny4games@gmail.com,contacto@venezuelaquiz.com,rafael.android@gmail.com,bano.nicola@gmail.com,boraborahi8@gmail.com,michaeljacksun80@gmail.com,kids@iabuzz.com,support@kiragames.com,gearandbulb@gmail.com,quelaba@gmail.com,titan.info.tech.co@gmail.com,android@abramania.com,webmaster@altarsoft.com,info@theoakteam.com,contact@opena.fr,yodesoft@gmail.com,parktae00@gmail.com,support@citygamesllc.com,rome.soft.co@gmail.com,mobigrow@gmail.com,sakuarloft@gmail.com,support@mindware.mobi,sudoku@genina.com,contact@bandagames.com,contact@turbochilli.com,xidea.creator@gmail.com,sue@bigbearentertainment.net,supportapp@magmamobile.com,zozo.apps.team@gmail.com,game7.winer@hotmail.com,support@budgestudios.ca,support@mobilityware.com,fred.jin0505@gmail.com,arun.saini@spicelabs.in,wangtingting.iris@gmail.com,games@lotum.de,support@criticalhitsoftware.com,help@mobirix.com,support@pyrosphere.net,puzzles@chris.boyle.name,arrawae@gmail.com,goforkeyboard@gmail.com,puzzleretreat@thevoxelagents.com,info@com2us.com,madrabbit.studio@gmail.com,contact@pinkpointer.com,developer@havos.co.uk,contact@droidcorp.net,mobilesupport@d3p.us,paintwo7@gmail.com,questions@intellijoy.com,support@splashpadmobile.com,android_support@chillingo.com,d2softapps@gmail.com,konamobile390@gmail.com,support@pixeldefenderspuzzle.com,android_support@ponos.co.jp,support.android@minimega.zendesk.com,eiswuxe@googlemail.com,umonistudios@gmail.com,submadinc@gmail.com,info@77sparx.com,support@secondgeargames.com,support@fixiclub.ru,tetrocrate@gmail.com,gun.rose@mail.com,apps@teachersparadise.com,aymobileru@gmail.com,tacoty.app@menue.com.cn,android@90123.com,gadt.labs@gmail.com,jerome@theincrediblecompany.com,info@neatjoy.com,tapdevstudio@gmail.com,happymealsupport@bonusstage.net,kidga.games@gmail.com,j2oasis@gmail.com,support@BrainiumStudios.com,albinoblacksheep@gmail.com,google.crazylabs@tabtale.com,jan.rychtar.dev@gmail.com,n225.zero@gmail.com,kidgame.edu@gmail.com,lintangprokoso@gmail.com,kenny.yue@gmail.com,mehmet@uysal.me,mafooly@gmail.com,fun4kids.apps@gmail.com,tecnico@hydra-networks.com,contact@phonato.com,AndroidFeedback@aceviral.com,htnmskdev@gmail.com,feedback@droidhang.com,willy.moonberg@gmail.com,support@jcbsystems.com,info@jellycrew.com,phonesnd@yandex.ru,pereiraasim@gmail.com,nebulabytes@gmail.com,smiledroid.android@gmail.com,kashikume.software@gmail.com,support+android@noodlecake.com,mukul@mokoolapps.com,mobsupport@comcept.jp,help@mctgame.com,zapletin.yevhenii@gmail.com,puissantapps@gmail.com,CutMyPuzzle@redbeachgames.com,piggybank.sftw@gmail.com,devsquare.com@gmail.com,scottadelman@gmail.com,peter.halada@androidaz.com,megasoft78@yahoo.it,support@espacepublishing.com,miyoshi581@gmail.com,support@icemochi.com,webmaster@melimots.com,theangrykraken@gmail.com,girblumk@gmail.com,Ronald.Geisler@AsgardSoft.de,morefunnersoftware@hotmail.com,harmonicitsolutions@gmail.com,aharmdroid@gmail.com,support@igoldtech.com,support@thewordsearchapp.com,jiuzhangtech@gmail.com,scmsoft+wordsnake@gmail.com,VictorMarkussen@gmail.com,contact@nrsmagic.com,phpnato@gmail.com,info@cooperapps.com,info@wixot.com,info@tanqbaymobile.com,support@mosteknoloji.com,megafaunasoft+anws@gmail.com,support@damoware.com,support@ithinkdiff.net,jdpapps@gmail.com,edu@michelange-workshop.org,arcaded@gmail.com,scmsoft+word@gmail.com,hello@oktomo.com,henning@devarai.com,mobilenewsdev@gmail.com,lipandes@gmail.com,community@appynation.com,sven.wordhero@gmail.com,contact@blacklightsw.com,skopjeapps@gmail.com,esapp@eleisongroup.com,lavapps@gmail.com,gospee.software@gmail.com,support@randomsaladgames.com,support@binteraktive.com,info@thedopplerfx.com,info@metaoption.com,ken@lxmx.com,harokosoft@gmail.com,support@cwigames.com,codestare@gmail.com,szokereso.jatek@gmail.com,mobiloids@gmail.com,msyskid0409@gmail.com,e3gamesmobile@gmail.com,dahl.brendan@brendandahl.com,support@wordcrack.com,info@xyclon.com.ar,android@kryptoniteapps.com,zeemeijer@gmail.com,support@kaptainapps.com,wetpalm@gmail.com,berkaypamir@gmail.com,littlebigplay@gmail.com,acogameinfo@gmail.com,contact@kloport.com,support@rockyou.com,ezjoy.feedback@gmail.com,info.myrias@gmail.com,fraserhay@bluecowgames.com,devaccounts@purpletalk.com,support@raketspel.se,WDMa@12gigs.com,jobberin7@gmail.com,word.kingdoms@gmail.com,support@lexathon.com,insipironxp@gmail.com,androidsmr@gmail.com,screene@gmail.com,info@gh.comze.com,info@walkme.pt,t.fuchsmartin@gmail.com,wordgames@interia.pl,fae.easy@gmail.com,jogoeusei@gmail.com,info@elf-games.com,feedback@newtzgames.com,support@xdebugx.net,info@isandroid.net,risoapps@gmail.com,support@lightwoodgames.com,ezeeideas@gmail.com,dieguilloapp@gmail.com,meercat012@yahoo.com,boriolapps@gmail.com,superfutureapps@gmail.com,info@blue44apps.com,stithmar@gmail.com,vmgames97@gmail.com,quality.software.feedback@gmail.com,apps@apostek.com,wazumbi@wazumbi.com,apps@casa-del-stifler.de,oddfractions@gmail.com,smallstepsllc@gmail.com,support@fubla.lv,sebas.leclerc@gmail.com,zubik@seznam.cz,kaushik@astroninfotech.com,xpresstechbell@yahoo.com,anjoyltd@gmail.com,adeveloper215@gmail.com,info@krizovkarsky-raj.cz,jobssoares@gmail.com,rescapi@hotmail.com,markercso@gmail.com,support@fuzzybug.co.uk,android@orcasoft.no,team1@bluefireventures.com,admin@stewdios.com,info@fivepumpkins.com,dutch85@hotmail.com,uclahklaw@gmail.com,fifthcolumnlabs@gmail.com,lollo.carlo@gmail.com,support@dry9.com,newshine@live.nl,redcellapps@gmail.com,apps@frosby.net,support@donkeysoft.ca,galaticdroids@gmail.com,info@article19.com,katsuaki.oshio@gmail.com,info@keystagefun.co.uk,support@aceviral.com,devteam@100woodlawn.com,inixthium2011@gmail.com,mongolcontent@gmail.com,joseph.ngdroid@gmail.com,supportandroid@apostek.com,support@hithot.cc,info@bullbitz.com,stylefieber@googlemail.com,salapaka@gmail.com,info@heftyapps.com,federica.rovere@gmail.com,v2beastzbroz@gmail.com,android.m.cubed@gmail.com,christian.liang@gmail.com,android@play.gs,littlebigplay@differencegames.com,digitalfunmedia@gmail.com,eazycode@gmail.com,ktydlackaapps@gmail.com,rivalbits@gmail.com,zsuri.android@gmail.com,support@scherpschutter.be,apps-android@withbuddies.com,campbellsteven80@gmail.com,info@educ8s.com,support@scribble-games.com,pixies78@tiscali.it,android.support@roboticode.co.uk,alfredo.baratta@gmail.com,android@tklabs.com,support@3dreamsgaming.com,clemquaq@gmail.com,support@afksoft.com,dam_bobjob@hotmail.com,powertopapps@gmail.com,one2android@gmail.com,info@balloonisland.com,pspiridonov@ukr.net,wordsearchapp@gmail.com,8bitmage.apps@gmail.com,goldeneagleandroid@yahoo.com,applimobile@gmail.com,simsamusa@gmail.com,cosmicslinky@gmail.com,appyhand@gmail.com,4orange.ts@gmail.com,tech.support@schoolzone.com,sinanyusufaydemir@gmail.com,support+androidmemorymatches@idcprojects.com,sofiablack6157@gmail.com,yggdrasilapps@gmail.com,admobmoj@googlemail.com,edusoftgame@gmail.com,mridhroid@gmail.com,Leopard2Mobile@gmail.com,contactmdsw@yahoo.com,support@outfit7.com,jigsaw@rottzgames.com,questions@kristanix.com,support@inertiasoftware.com,developers@togeproductions.com,per.haglund@appfamily.se,support@absolutist.com,support@bubadu.com,d.zering@jourist.de,nvluyen@gmail.com,support@filimundus.se,info@ptowngames.com,dzoni83@gmail.com,bellemarium@gmail.com,ContactUs@GiggleUp.com,boss@puzzleboss.com,support@pwgames.co,nadiataha23@gmail.com,ContactUs@giggleup.com,contact@hedgehogacademy.com,wizardstime@gmail.com,jigsaur@gmail.com,gilesluca@gmail.com,chencaiying1984@163.com,jeegsoft@gmail.com,msmg1234@gmail.com,vphdi.ionline123us@gmail.com,yaboacomp@gmail.com,info@flyingsoftgames.com,support@pusz.net,eng.mhd.jazaery@hotmail.com,damianz.apps@yahoo.com,drcom.ro@gmail.com,android.dagiel@gmail.com,orionsmason@gmail.com,lstyr188@gmail.com,oskanaan@gmail.com,pango@studio-pango.com,dappledore@gmail.com,developer@ta-dah-apps.com,mauro@mmegames.com,info@codeartmobile.com,fungmyron@gmail.com,yangxiaodong2010@gmail.com,bladeofgame@gmail.com,jacobotibaquira@gmail.com,johny2000uwb@gmail.com,ferran.tebe@gmail.com,batparker@naver.com,support@moobilelab.com,mohammadkhaled90@gmail.com,support@familyplay.co,radoslaw.szwarc@szwarcsoft.com,mykola.dev@gmail.com,info@wombiapps.com,support@gabysoft.com,adbolapp@gmail.com,miroslaw@kapalka.pl,livejigsaws@differencegames.com,kesara467@gmail.com,MillardGodin212@gmail.com,info@multipie.co.uk,support@codore.com,jonathanbullet19@gmail.com,skolalingua@gmail.com,blakitinfo@gmail.com,018lady@gmail.com,lily.yr.tu@gmail.com,puzzlecommander@gmail.com,developer@potrus.pl,tksoda@naver.com,peterjulianovpetrov@gmail.com,ychmin@gmail.com,yexiangpan1988@163.com,cicmilici@gmail.com,newkmo99@gmail.com,megafaunasoft+anwj@gmail.com,androidforever321@gmail.com,jozsefcsizapps@gmail.com,kavrakids@gmail.com,support@prontomind.com,magnetgene@gmail.com,support+android@coragames.com,info.brainyapestudio@gmail.com,2157755@gmail.com,apps@insplisity.com,divmobvn@gmail.com,kvosilv@gmail.com,qapps.qmul@gmail.com,appli@re-dinc.co.jp,info@differencegames.com,houcine.romdhane@magiccalc.net,support@fairyengine.com,bluelex7@gmail.com,flappy@som3on3.com,info@lugesoft.com,info@izmo.com.tr,takarogames@gmail.com,faddies.sofizar@gmail.com,contact@danielalbu.com,duy@1112group.com,mika.indie@gmail.com,mobease.team@gmail.com,evui.vn@gmail.com,mrekion@hotmail.com,mawikamaji@gmail.com,agarwal.sudhir@gmail.com,help@jinfra.com,josejibi@gmail.com,fonwap@gmail.com,al_mughairi@hotmail.com,carloshdzg@gmail.com,pogung177@gmail.com,imkits@gmail.com,lupinmagic@gmail.com,v_a_singh@hotmail.com,niscama@alice.it,kurngs@gmail.com,daniel@nexyu.com,notification@digitalbuzz.com,best_libra_4u@hotmail.com,hamoosh@msn.com,robife@hotmail.com,huzefam@gmail.com,bafna.sanket@gmail.com,siopon@gmail.com,prashant.eb@gmail.com,vince@vindi.co.nz,bassinside@gmail.com,otenyanom@gmail.com,decima.us@gmail.com,contact.mayabra@gmail.com,nsvsm08@gmail.com,sushilroy22@gmail.com,nurmuhammad.abdurashidov@gmail.com,verticalbbs@gmail.com,vanyhsn@gmail.com,manhnguyen.dev@gmail.com,tp.modmanage@gmail.com,siccchris@yahoo.com,smartpercent@gmail.com,vinod@56060.in,help.siddit@gmail.com,ggdevstudios@gmail.com,kondasiddhartha@gmail.com,sachi.elibrary@gmail.com,siddjain@live.com,siddharth.sivaraman10@gmail.com,shahsid94@yahoo.co.in,rajkotraja99@gmail.com,echoshardproductions@gmail.com,pouhelp@gmail.com,onetouchgame@gmail.com,sergeiozerov@gmail.com,support@djinnworks.at,support@jaaru.com,sagego@gmail.com,adamrocker@gmail.com,Muhendis1223@gmail.com,vnretro@gmail.com,endorfine19@gmail.com,jerryjiang1128@gmail.com,marco.oman69@gmail.com,cdremrah@gmail.com,rongjun.y@gmail.com,nail2001@gmail.com,ducanhtuvu@gmail.com,aljazz.vidmar@gmail.com,admin@portalmarketbuy.com,info@neridavide.it,pysmartbd@gmail.com,gagoehx@gmail.com,peter_weiss@gmx.de,androiddev@spicelabs.in,support@optimesoftware.com,support@virtuesoft.com,meesoftware@gmail.com,support@soodexlabs.com,info@apprope.com,sathuthespark89@gmail.com,admin@ahead-solutions.com,mail@irodriguez.es,lmaosoft.com@gmail.com,support@nextwebgeneration.de,aryavratinfotech@gmail.com,info@onecool.se,support@mobivention.com,omkar.tadepalli@caprusit.com,android@alexyu.fr,breaker1994@mail.ru,android@sonic-the-hedgehog.com,itsolutions.games@gmail.com,android.support@gameloft.com,ricardo.alves.developer@gmail.com,adeel.k.siddiqui@gmail.com,doubleblacksoftware@gmail.com,ragemodestudios@gmail.com,alchimedia.hu@gmail.com,krembosoft@gmail.com,rubberKB@gmail.com,nicekhj3@gmail.com,intorion8@gmail.com,contact@colletjb.com,postelmansroel@gmail.com,developer@zyksa.com,jessicapiresf@gmail.com,contact@balofogames.com.br,android.support@marlove.net,android@lavoisy.fr,jajaz.org@gmail.com,jbizandroid@gmail.com,cavedawes@blueyonder.co.uk,contact@advancedtinylab.com,thundrix@online.de,umeshbunty1@gmail.com,cristais.soft@gmail.com,darthsith81@gmail.com,dev.android@mangotechno.com,support@firecrackersw.com,alexandru.despina@gmail.com,kaiowa@gmail.com,hakimsjo@gmail.com,macaque.games@gmail.com,lios.appdev@gmail.com,mobilentsoftware@gmail.com,botijonline@gmail.com,gordondevelop@gmail.com,slaminsoftware@gmail.com,support@kimto.se,feedback@hangwith.com,jlcarrasco1990@gmail.com,miroslav.milev@hotmail.com,android@manipalsystems.com,dwalleser@googlemail.com,smappdev1@gmail.com,dany.poplawec@oxymob.fr,webmaster@wapfrog.com,filipeuff@gmail.com,duane.odom@gmail.com,contact@manuelciosici.com,support@jakkl.com,mooneer7@gmail.com,aptomassini@gmail.com,gamesignacio@yahoo.es,dvloper.apps@gmail.com,juro157@gmail.com,shivapr1974@yahoo.com,bigzurgames@gmail.com,akumayken@gmail.com,thejaymac@gmail.com,appinadvance@gmail.com,mynumis10@gmail.com,eu.evgb@googlemail.com,Contact@playtouch.net,kenneth.lemieux@gmail.com,info@repsis.com,she.loves.apps@gmail.com,support@portdusk.com,techcomsolution60@gmail.com,mlafuente@gmail.com,sion@sionco.com,hellohovelgames@gmail.com,arysoft.play@gmail.com,dreamandroidapps@gmail.com,vovatal7@gmail.com,harapani@harapanindonesia.com,bunniessoft@gmail.com,marudroid@maru.jp,roflnewbcreations@gmail.com,support@senses.co.jp,team@braincrumbz.com,info@urara-works.co.jp,htkhoi2@gmail.com,android@generamobile.com,admin@androidrich.com,android@pixas.de,zatton.yuki@gmail.com,support@tiddagames.com,corollarycomputing@gmail.com,mobile@netigen.pl,piotr@netaddict.pl,phamchang1993@gmail.com,amarket@sgn.com,intriga.games@gmail.com,gtappsdev@gmail.com,horea.bucerzan@gmail.com,enzo.pellecchia@gmail.com,jonas.americo10@gmail.com,varankh2307@gmail.com,hellokorean.dev@gmail.com,danramirezandroid@gmail.com,liutauras.apps@gmail.com,info@kneego.org,fener.dfi@gmail.com,bozzaisoftware@gmail.com,spiele.fuer.kinder.acc@gmail.com,mvgrach@gmail.com,atomica.dc@gmail.com,support@arclitebd.com,android@wintrino.com,spiraltimestudio@gmail.com,byril.company@gmail.com,emadoz84@gmail.com,support@vmsoft-bg.com,mailto.suneethab@gmail.com,appygo.labs@gmail.com,noambehar1@gmail.com,support@tmsoft.com,hidemyass01@gmail.com,tacticalhockey@gmail.com,info@incredibleapp.com,info@brodski.com,agocuGames@gmail.com,mobile@g6solutions.com,escogitare@gmail.com,mail@tazcotech.com,harokosoft@harokosoft.com,almatime.soft@gmail.com,support@giraffe-games.com,androidmarket@dhanew.com,appspacer@gmail.com,michal@bukacek.cz,crossfield3@gmail.com,titico.lab@gmail.com,classicgamesemail@gmail.com,brad.seay@gmail.com,support@flipside5.com,skybonep@gmail.com,mivanraz@gmail.com,takuappme@gmail.com,thomas@mycibty.com,info@softwaretechnology.com,topappsonline@gmail.com,android@qeep.net,nwmotogeek@gmail.com,nickolyanstudios@gmail.com,kaushikandroidgames@gmail.com,james6324178@gmail.com,gstolarov@gmail.com,tictactics@hiddenvariable.com,mightycometstudios@gmail.com,gagabunch@gmail.com,potatoideas@gmail.com,eserikov7@gmail.com,admin@ilikedroid.com,joycloudinc@gmail.com,tyttoot@gmail.com,support@epitosoft.com,tictactoeforspeed@gmail.com,jumyapps@gmail.com,wizdom125@gmail.com,teuf.labs@gmail.com,support@mewli.com,oldschoolgamesapps@gmail.com,info@libellentech.com,viz.soft.ad@gmail.com,branapplications@gmail.com,oliversride@gmail.com,support@fatarat.com,info@shockblastapps.com,dailyh.android@gmail.com,mobile@ilumnis.com,fennasnogothrim@gmail.com,blueskychat@gmail.com,osamahq@gmail.com,gremlin123321@gmail.com,mikegolpda@gmail.com,salilmalik92@gmail.com,BlackSheepGamesAB@gmail.com,support@skiller-games.com,feedback4apps@gmail.com,info@2dlevel.com,gserp1983@gmail.com,toujeni.cherif@gmail.com,csuarez@waspgroup.com,tosto86@gmail.com,vidur.jain@gmail.com,write@eatplaylove.it,luigifiorelli91@gmail.com,skyacteck@gmail.com,nikhade.aditya@gmail.com,nagamatu@gmail.com,mitesh007@gmail.com,android@pcsalt.com,info@cannysoftware.com,syncrom@syncrom.com,DolceGames@gmail.com,info@emersoft.net,galvezgames@gmail.com,alexeykcom@gmail.com,shangma.1st@gmail.com,support@scoutant.org,keyja.com@gmail.com,igosttechnologies@gmail.com,harnessjim@gmail.com,sqr3labs@gmail.com,ap@apcod3.com,a.s.solovyov@gmail.com,apps@darshantechnologies.com,support@espritpixels.com,sharathkesireddy@gmail.com,ktysyachnyi@gmail.com,ekugar@gmail.com,android@blasterbit.com,intelligenttictactoe@gmail.com,support@buckbuckgames.com,dws.dev@gmail.com,games@rhmgames.com,luca.mtudor@gmail.com,bluemonkeyplay@gmail.com,bse71@bse71.ru,appsbezy@gmail.com,rvappstudios@gmail.com,support@fitzgeraldsoftware.com,sudhakar.kanakaraj@gmail.com,firezenk@gmail.com,ttt@blub.it,VolodymyrVients@gmail.com,foxinaboxapps@gmail.com,support@waysandmeanstechnology.com,gamemakersrd@gmail.com,feedback@redialstudios.com,contact@chicktactoe.com,mengxiangpingx@gmail.com,ricardo.valerio@gmail.com,android.xam@gmail.com,pierre.felgines@gmail.com,llpartners@llpartners.co.uk,olcinium.software@gmail.com,androcalc@gmail.com,rishabhjainwit5@gmail.com,sash33rus@gmail.com,cosineman@gmail.com,developer5@ildarn.com,xottabut@gmail.com,acs.lmv@gmail.com,android@appslight.com,support@star-arcade.com,couldsys@gmail.com,mathis.steph@gmail.com,admin@quarterdeckgames.com,sergnaum2@gmail.com,dimitrilanoe35@gmail.com,bas9112@gmail.com,sjonesgso@gmail.com,rohit.sah.app.dev@gmail.com,info@mobisoft.company,kartikchoudhury@gmail.com,dmc074@gmail.com,pennypiecedevelopment@hotmail.com,bsdrago@xuti.net,dylberm@gmail.com,tapan.desai007@gmail.com,vampireneo.apps@gmail.com,contactus@yogyaland.com,k@kuvav.com,KNIapps69@gmail.com,ultradevmobile@gmail.com,ferolobu1992@gmail.com,androidsamsstuff@gmail.com,icrwebagency@gmail.com,dabkechinmay@gmail.com,er.divyesh.shani@gmail.com,nekobukiya@gmail.com,meiseijoho@gmail.com,ssoft8@hotmail.com,sairamsaishyamshirdi@gmail.com,mgupta@mindrootstechnologies.com,k.makishi22@gmail.com,maksym.dovbnia@gmail.com,tejachilled@gmail.com,whycatsang@gmail.com,android@tk-impacts.jp,info@andela.eu,peggys.garden2@gmail.com,awnrysoftware@gmail.com,apps@pixatel.com,s0b3rapps@gmail.com,andrew.palamar@gmail.com,abhishekchawla26@gmail.com,ran.neto@gmail.com,PatrikBednarik070@gmail.com,contact@aimlesscreativity.com,pk34@live.com.mx,patel.s.parth@gmail.com,portiexc@gmail.com,mobilplug@gmail.com,bestfreeappandroid@gmail.com,i.arsen@bk.ru,info@adcoms.net,nithokian@zoho.com,info@gtesoftware.com,mattlmattlmattl@gmail.com,ezasoba@gamecontester.org.ua,drtikov@gmail.com,Vijay053@gmail.com,juanjavierrg@gmail.com,Ehaney0429@gmail.com,luismigueldelcorral@gmail.com,support@catrobat.org,developer.schindeler@gmail.com,powerappsdev14@gmail.com,support@fusionlogic.co.uk,pacoabato@gmail.com,support@ludei.com,dev.freepo@gmail.com,fabril.and@hotmail.com,support@faizvisram.com,android@cocosjungle.com,danijerakic26@gmail.com,support@mathworkoutgame.com,support@yhomework.com,Rusion@bk.ru,mobiloids@gmail.com,support@troubi.com,bjoern.voigt@gmail.com,support@ixl.com,classicgamesemail@gmail.com,questions@intellijoy.com,support@novellectual.com,ranviclab@gmail.com,contact@blacklightsw.com,apps@teachersparadise.com,kjhg6030@naver.com,polychromesoftware@gmail.com,support@igoldtech.com,service@mathopen.com,supportabmath@lehovetzki.fr,kontakt@paridae.pl,PeakselGames@gmx.com,taptolearn@gmail.com,nvluyen@gmail.com,support@babycortex.com,supportabmath@lehovetzki.fr,info@nuevalgo.com,360sages@gmail.com,pconlinegamesmail@gmail.com,info@oddrobo.com,ana.redmond@gmail.com,info@adcoms.net,fallinginc@gmail.com,divmobvn@gmail.com,dotocto@gmail.com,support@onebillion.org.uk,support@mathworkoutgame.com,rcalmant@yahoo.fr,info@flexymind.com,coolmangoplay@gmail.com,turtlelabs.info@gmail.com,info@psinternet.de,PeakselGames@gmx.com,supportabmath@lehovetzki.fr,info@ramkystech.com,divmob.com@gmail.com,apps@teachersparadise.com,support@jumpstart.com,coolrohit.172@gmail.com,taptolearn@gmail.com,support@TabTale.com,info@friskypig.com,mathdoodle.android@gmail.com,bjoern.voigt@gmail.com,andrew.brusentsov@gmail.com,contact@sunnysideblue.com,divmobvn@gmail.com,info@abcya.com,support@mathpanic.com,rome.soft.co@gmail.com,ContactUs@GiggleUp.com,support@kindergartenmobile.com,questions@intellijoy.com,kiddo.math@gmail.com,blackstar.feedback@gmail.com,info@doubleddev.com,conntekdev@gmail.com,honeybee.apps@mobyport.com,ranviclab@gmail.com,contact@edupad.com,greenbutton75@gmail.com,iKaesTech@gmail.com,greenbutton75@gmail.com,interactioneducation@gmail.com,applimobile@gmail.com,kurrain@gmail.com,angel.ivorra@gmail.com,kurrain@gmail.com,hoffman.jon@gmail.com,leochen770@gmail.com,mailto.msaraswathi@gmail.com,sales@hitb.eu,contact@edupad.com,kidga.games@gmail.com,bill@mobiadage.com,arrawae@gmail.com,educagames.es@gmail.com,info@captivegames.com,delaroche.jeanbaptiste@gmail.com,ween306@gmail.com,divmobvn@gmail.com,divmob.com@gmail.com,maysa.erkol@gmail.com,brainbowdevelopment@gmail.com,info@oddrobo.com,support@osc-apps.com,mSurfLab@gmail.com,this.is.lance.miller@gmail.com,contact@edupad.com,MathPandaApp@gmail.com,everykidsapps@gmail.com,this.is.lance.miller@gmail.com,info@tititaa.com,breathe@zengardenapps.com,everykidsapps@gmail.com,ana.redmond@gmail.com,a3bgame@gmail.com,support@splashmath.com,feedback@bedtimemath.org,contact@spinfall.com,support@chifro.com,contact@edupad.com,MathPandaApp@gmail.com,support@penguinapps.com.au,support@beiz.com,PeakselGames@gmx.com,smandroiddeveloper@gmail.com,delaroche.jeanbaptiste@gmail.com,mobileregistration@zingma.com,support@beyondlearning.co.uk,info@thumbstorm.com,play@testaday.com,admin@learnersplanet.com,uclahklaw@gmail.com,bill@mobiadage.com,fun4kids.apps@gmail.com,pazvanti2003@gmail.com,support@chifro.com,appsfree2014@gmail.com,info@arithmo.com,sumitlad1986@gmail.com,info@adcoms.net,support+mif@4enjoy.com,hadiyanto.khang@gmail.com,support@ilearnwith.com,support@jcbsystems.com,easyitis@easyitis.com,demian@become-secure.net,support@fancygames.net,wooin0707@naver.com,francisco@appscapital.com,info.myrias@gmail.com,neumediatechnology@gmail.com,support@atisprim.net,contact@edupad.com,rmagdev@gmail.com,ehsan.mahmood4@gmail.com,ronny@dotwdg.com,matt@spinlight.com,cottonikr@gmail.com,realdreamtale@gmail.com,creatorsepark@gmail.com,info@cellecgames.com,max.faradize@gmail.com,contact@aris-vn.com,ttoingc@naver.com,info@oddrobo.com,angelo10027@gmail.com,play@testaday.com,gavapps@gmail.com,PeakselGames@gmx.com,LipApps.Android@gmail.com,kidsyq@gmail.com,office@jumpido.com,martin.majowski@googlemail.com,musa.cavus@googlemail.com,PeakselGames@gmx.com,codedhrj@gmail.com,bagopian@gmail.com,kurrain@gmail.com,info@sen-sei.in,support@beiz.com,support@maths4uk.co.uk,support@fancygames.net,padmakarojha@gmail.com,mathbugaboo@gmail.com,remarkable.apps@gmail.com,support@beiz.com,support@osc-apps.com,thanhquan1512@gmail.com,support@jozapps.com,support@osc-apps.com,developer@audaciagames.com,simplexdevs@gmail.com,support@epicpixel.com,bm11@kayac.com,thinkinggarden.apps@gmail.com,support@jumpstart.com,support@adventure.com,fun4kids.apps@gmail.com,info@motionmathgames.com,support@osc-apps.com,info@keystagefun.co.uk,contact@spinfall.com,support@splashmath.com,tigrisent@gmail.com,support@osc-apps.com,support@troubi.com,android@poissonrouge.com,support@whitneyapps.com,support@kindergartenmobile.com,kurrain@gmail.com,developer@lestroiselles.com,info@akimis.com,persellin_zev_@hotmail.com,divmobvn@gmail.com,principalapps@gmail.com,ongiavotu@gmail.com,jovana.jovana1984@gmail.com,info@wspublishinggroup.com,yogasaikrishna@gmail.com,admin@freecloud.com,support@diespiderdie.com,yusup2702@gmail.com,appsunlimited88@gmail.com,bugshootscores@gmail.com,jstay@mergemobile.com,nvluyen@gmail.com,prosperity4dev@gmail.com,shivapr1974@yahoo.com,everykidsapps@gmail.com,honeybee.apps@mobyport.com,sudhirmangla@gmail.com,support@apps4android.org,mktamir@gmail.com,support@betarom.net,gamesforkidsufsc@gmail.com,mathsrings@gmail.com,support@jumpstart.com,andres.eurodr@gmail.com,ashish0309@gmail.com,leolau@joyaether.com,remarkable.apps@gmail.com,support@maruram.com,trinhlbk1991@gmail.com,x.xmassdeveloper@gmail.com,android-wombi@dlmw.se,woodencloset@gmail.com,android@spielend-lernen-verlag.de,inefficientcode@gmail.com,support@beiz.com,admin@learnersplanet.com,admin@learnersplanet.com,myfamilyrules@gmail.com,mathbugaboo@gmail.com,csaxby@mac.com,homenetgames@gmail.com,xxxcoltxxx@gmail.com,johnnyoc3@gmail.com,mincho.kolev@gmail.com,mourlam@ancientsheep.com,ezeeideas@gmail.com,support@wordcrack.com,themelisx@gmail.com,support@juxtalabs.com,dmitry0208@gmail.com";

        $emails_array = explode(",", $emails);

        $final_emails_array = array_unique($emails_array);
        $final_emails_string = implode("<br />", $final_emails_array);
        echo $final_emails_string;
echo "<br /><br /><br />";
        echo count($final_emails_array);
        echo "<pre>";
        print_r($final_emails_array);
        echo "</pre>";
    }

}

