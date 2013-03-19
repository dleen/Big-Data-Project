library(ggplot2)
library(reshape2)
library(MASS)

library(directlabels)

data <- read.csv('./results-all.csv', header=FALSE)

colnames(data) <- c("Method", "Cores", "Error", "Time")

meth <- unique(data[["Method"]])

newdata <- data.frame()

for(c in meth) {
  print(c)
  a <- data[data["Method"] == c,]
#   one <- a[1,"Time"]
  one <- mean(a[a["Cores"] == 1, "Time"])
  j = 1
  for(i in 1:6) {
#     j = j - 1
    while(a[j, "Cores"] == i && j < 307) {
      a[j,"Time"] <- one / a[j,"Time"] 
      j = j + 1
    }
  }
  newdata <- rbind(newdata, a)
}

newdata["Method"] <- factor(newdata[["Method"]])

width = 5
height = 4

se <- function(x) {
  sqrt(var(x) / length(x))
}


pdf('./speedup_normal.pdf', width=width, height=height)

theme_set(theme_minimal(base_size = 12))

p1 <- ggplot(data=newdata[newdata["Method"] == "NORMAL",], aes(x=Cores, y=Time))
# p1 <- p1 + geom_line(stat="summary", fun.Time="mean", color="red", aes(group=Cores))
p1 <- p1 + geom_point()
# p1 <- p1 + stat_summary(fun.y=mean, fun.ymin=se, fun.ymax=se, geom="errorbar", color="red")
p1 <- p1 + stat_summary(fun.data=mean_sdl, geom="errorbar", color="red")

p1 <- p1 + xlab('Number of cores')
p1 <- p1 + ylab('Speedup')
p1 <- p1 + ggtitle('Hogwild! Speedup')

print(p1)

dev.off()

pdf('./error_normal.pdf', width=width, height=height)

p2 <- ggplot(data=newdata[newdata["Method"] == "NORMAL",], aes(Cores, Error))
p2 <- p2 + geom_point()
p2 <- p2 + xlab('Number of cores')
p2 <- p2 + ylab('Error')
p2 <- p2 + ggtitle('Hogwild! Error')

print(p2)

dev.off()

pdf('./speedup_others.pdf', width=12, height=height)

# p3 <- ggplot(data=newdata[newdata["Method"] != "NORMAL",], aes(Cores, Time, group=Method, color=Method))
p3 <- ggplot(data=newdata, aes(Cores, Time, group=Method, color=Method))
p3 <- p3 + facet_wrap(nrow=1, ~Method)
p3 <- p3 + geom_point()
p3 <- p3 + xlab('Number of cores')
p3 <- p3 + ylab('Speedup')
p3 <- p3 + ggtitle('Hogwild! Speedup')
# p3 <- p3 + theme(panel.margin = unit(c(1,10,1,1), "cm"))
# print(direct.label.ggplot(p3, list("last.qp")))

print(p3)

dev.off()

pdf('./error_others.pdf', width=width, height=height)

p4 <- ggplot(data=newdata[newdata["Method"] != "NORMAL",], aes(Cores, Error, group=Method, color=Method))
p4 <- p4 + geom_point()
p4 <- p4 + xlab('Number of cores')
p4 <- p4 + ylab('Training Error')
p4 <- p4 + ggtitle('Hogwild! Error')
# print(direct.label.ggplot(p4, list("last.qp")))

print(p4)

dev.off()