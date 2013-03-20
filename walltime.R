library(ggplot2)
library(reshape2)

library(plyr)

data1 <- read.csv('./results-wall1.csv', header=FALSE)
data2 <- read.csv('./results-wall2.csv', header=FALSE)
data3 <- read.csv('./results-wall3.csv', header=FALSE)
data4 <- read.csv('./results-wall4.csv', header=FALSE)
data5 <- read.csv('./results-wall5.csv', header=FALSE)
data6 <- read.csv('./results-wall6.csv', header=FALSE)

data <- rbind(data1, data2, data3, data4, data5, data6)

colnames(data) <- c("Method", "Cores", "Error", "WallTime")

meth <- unique(data[["Method"]])

data["WallTime"] <- 100*floor(data["WallTime"]/100) + 1
data <- data[order(data["WallTime"]),]

# mymean <- function(group) {
#   
# }

newdata <- ddply(data, c("Method", "WallTime"), summarise, avgErr = mean(Error))


height <- 5
# pdf('./walltime.pdf', width=12, height=height)
# 
# theme_set(theme_minimal(base_size = 12))
# 
# p <- ggplot(data=data, aes(WallTime, Error, group=Method))
# p <- p + geom_point(alpha=0.33)
# p <- p + stat_smooth()
# 
# p <- p + facet_grid(~Cores)
# p <- p + coord_cartesian(ylim=c(0.1705, 0.1725))
# # p <- p + scale_y_log10()
# 
# print(p)
# 
pdf('./walltime_6.pdf', width=12, height=5)

theme_set(theme_minimal(base_size = 12))

p <- ggplot(data=newdata, aes(WallTime, avgErr, group=Method))
p <- p + geom_line()
p <- p + facet_grid(~Method)
p <- p + coord_cartesian(ylim=c(0.1705, 0.185))
p <- p + xlab("Walltime (ms)")
p <- p + ylab("Test Error")
# p <- p + scale_y_log10()

print(p)

dev.off()

# pdf('./walltime_2d.pdf', width=5, height=height)
# 
# data6 <- data[data["Cores"] == 6,]
# 
# data6norm <- data6[data6["Method"] == "NORMAL",]
# data6replicate <- data6[data6["Method"] == "REPLICATE",]
# 
# # newdata <- merge(data6norm, data6replicate, all=TRUE, by=NULL)
# # colnames(newdata) <- c("M1", "C1", "E1", "W1", "M2", "C2", "E2", "W2")
# 
# pc <- ggplot(data=newdata, aes(x=WallTime.x, y=WallTime.y))
# pc <- pc + geom_tile(aes(fill=log10(Error.x/Error.y), width=100, height=100, hpad=0, vpad=0))
# # pc <- pc + scale_fill_brewer(type="div")
# 
# print(pc)
# 
# dev.off()
