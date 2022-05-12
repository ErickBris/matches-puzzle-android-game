<!DOCTYPE html>
<html lang="en" xmlns:og="http://ogp.me/ns#" xmlns:fb="https://www.facebook.com/2008/fbml">
    <head>       
        <meta charset="utf-8" />
        <title>Who can fix this equation?</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="description" content="Help me to fix this equation" />

        <meta property="og:image" itemprop="image primaryImageOfPage" content="<?php echo base_url(); ?>global/views/front/img/app_icon_lrg.png" />
        <meta property="og:image:type" content="image/png" />
        <meta property="og:image:width" content="200" />
        <meta property="og:image:height" content="200" />
        
        <!-- The styles -->
        <link id="bs-css" href="<?php echo base_url(); ?>global/views/back/css/foot-classic.css" rel="stylesheet" />
        <link href="<?php echo base_url(); ?>global/views/back/css/foot-responsive.css" rel="stylesheet" />
        <link href="<?php echo base_url(); ?>global/views/back/css/charisma-app.css" rel="stylesheet" />
        <link href="<?php echo base_url(); ?>global/views/front/css/style.css" rel="stylesheet" />

        <!-- The HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->

        <!-- The fav icon -->
        <link rel="shortcut icon" href="<?php echo base_url(); ?>global/views/back/img/favicon.ico">

    </head>

    <body>
        <div class="container-fluid">
            <div class="row-fluid">

                <div class="row-fluid">
                    <div class="span12 center">
                        <h2 style="color: #800000;">Who can fix this equation?</h2>
                    </div><!--/span-->
                </div><!--/row-->

                <div class="row-fluid">
                    <div class="center login-box">
                        <h3 id="help">Move <?= ($level->le_moves == 1) ? "1 match" : "2 matches"; ?> to fix the equation</h3>
                        <div id="equation">                            
                            <br/><br/>
                            <div id="xValue">
                                <?php
                                $class = "";
                                ?>
                                <?php $i = 0;
                                foreach ($x_matches as $xm) { ?>
                                    <?php
                                    if ($i == 1 || $i == 4) {
                                        $class = "matchLeft";
                                    } elseif ($i == 2 || $i == 5) {
                                        $class = "matchRight";
                                    } else {
                                        $class = "matchHoriz";
                                    }
                                    ?>
                                    <span id="xVal<?= $i; ?>" class="<?= $class; ?>"><img src="<?php echo base_url(); ?>global/views/front/img/<?= $xm; ?>.png" /></span>
                                    <?php $i++;
                                } ?>
                            </div>                            

                            <div id="operator">
                                <span><img src="<?php echo base_url(); ?>global/views/front/img/<?= ($level->le_operator == "+") ? "plus" : "minus"; ?>.png" /></span>                                
                            </div>

                            <div id="yValue">
                                <?php $i = 0;
                                foreach ($y_matches as $ym) { ?>
                                    <?php
                                    if ($i == 1 || $i == 4) {
                                        $class = "matchLeft";
                                    } elseif ($i == 2 || $i == 5) {
                                        $class = "matchRight";
                                    } else {
                                        $class = "matchHoriz";
                                    }
                                    ?>
                                    <span id="yVal<?= $i; ?>" class="<?= $class; ?>"><img src="<?php echo base_url(); ?>global/views/front/img/<?= $ym; ?>.png" /></span>
                                    <?php $i++;
                                } ?>                                
                            </div>

                            <div id="equal">
                                <span id="equ0" style="display: inline-block;margin-bottom:20px;"><img src="<?php echo base_url(); ?>global/views/front/img/match_on.png" /></span>
                                <span id="equ1"><img src="<?php echo base_url(); ?>global/views/front/img/match_on.png" /></span>                            
                            </div>

                            <div id="rValue">
                                <?php $i = 0;
                                foreach ($r_matches as $rm) { ?>
                                    <?php
                                    if ($i == 1 || $i == 4) {
                                        $class = "matchLeft";
                                    } elseif ($i == 2 || $i == 5) {
                                        $class = "matchRight";
                                    } else {
                                        $class = "matchHoriz";
                                    }
                                    ?>
                                    <span id="rVal<?= $i; ?>" class="<?= $class; ?>"><img src="<?php echo base_url(); ?>global/views/front/img/<?= $rm; ?>.png" /></span>
                                    <?php $i++;
                                } ?>                                
                            </div>
                        </div>
                        <br />
                        <br />                                          
                        <br />                                                                  
                        <p style="color: #FFFFFF;font-size: 1.4em"><b>Enjoy Matches Puzzle game!</b></p>
                        <img src="<?php echo base_url(); ?>global/views/front/img/app_icon.png" width="48px" height="48px" />
                        <p><img src="<?php echo base_url(); ?>global/views/front/img/googleplay.png" width="32px" height="32px" /><a href="<?php echo $market_link->us_market_link; ?>" onclick="wndow.open(this.href); return false;">Install from google play</a></p>
                    </div><!--/span-->
                </div><!--/row-->
            </div><!--/fluid-row-->

        </div><!--/.fluid-container-->     
    </body>
</html>

