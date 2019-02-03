CREATE TABLE `metricamount` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `edgenode` varchar(45) NOT NULL,
  `experimentname` varchar(45) NOT NULL,
  `metric` varchar(45) NOT NULL,
  `variation` int(11) DEFAULT NULL,
  `amountof` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13551 DEFAULT CHARSET=latin1;

CREATE TABLE `metricamountandtimes` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `edgenode` varchar(45) NOT NULL,
  `experimentname` varchar(45) NOT NULL,
  `metric` varchar(45) NOT NULL,
  `variation` int(11) DEFAULT NULL,
  `amountof` int(10) unsigned NOT NULL,
  `computationinmillis` int(10) unsigned NOT NULL,
  `computationinseconds` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

CREATE TABLE `metricamountandvalue` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `edgeNode` varchar(45) NOT NULL,
  `experimentName` varchar(45) NOT NULL,
  `metric` varchar(45) NOT NULL,
  `variation` int(11) DEFAULT NULL,
  `amountOf` int(10) unsigned NOT NULL,
  `valueOf` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `metriccomputationtime` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `edgenode` varchar(45) NOT NULL,
  `experimentname` varchar(45) NOT NULL,
  `metric` varchar(45) NOT NULL,
  `variation` int(11) DEFAULT NULL,
  `start` datetime NOT NULL,
  `finish` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
