<!DOCTYPE html>
<html lang="en">
    <head>       
        <meta charset="utf-8">
        <title>Who know this logo?</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="Who knows this logo?">       

        <!-- The styles -->
        <link id="bs-css" href="<?php echo base_url(); ?>global/views/back/css/foot-classic.css" rel="stylesheet">
        <link href="<?php echo base_url(); ?>global/views/back/css/foot-responsive.css" rel="stylesheet">
        <link href="<?php echo base_url(); ?>global/views/back/css/charisma-app.css" rel="stylesheet">


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
                    <div class="span12 center login-header">
                        <h2>Who knows this logo?</h2>
                    </div><!--/span-->
                </div><!--/row-->

                <div class="row-fluid">
                    <div class="well span5 center login-box">                        

                        <table>
                            <tr>
                                <th>#</th>
                                <th>x</th>
                                <th>operator</th>
                                <th>y</th>
                                <th>=</th>
                                <th>r</th>
                            </tr>
                            <?php $i = 1; foreach($levels as $eq) { ?>
                            <tr>
                                <td><?= $i; ?></td>
                                <td><?= $eq['le_x_value']; ?></td>
                                <td><?= $eq['le_operator']; ?></td>
                                <td><?= $eq['le_y_value']; ?></td>
                                <td>=</td>
                                <td><?= $eq['le_r_value']; ?></td>                                
                            </tr>
                            <?php $i++; } ?>
                        </table>                       
                    </div><!--/span-->
                </div><!--/row-->
            </div><!--/fluid-row-->

        </div><!--/.fluid-container-->     
    </body>
</html>

